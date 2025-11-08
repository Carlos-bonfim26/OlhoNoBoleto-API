package com.example.OlhoNoBoleto.service;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.example.OlhoNoBoleto.dto.boleto.BoletoResponseDTO;
import com.example.OlhoNoBoleto.dto.boleto.BoletoValidateRequestDTO;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;
import com.example.OlhoNoBoleto.repository.ReportRepository;

@Service
public class BoletoService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;
    @Autowired
    private BeneficiarioService beneficiarioService;
    @Autowired
    private ReportRepository reportRepository;

    public BoletoResponseDTO validarBoleto(BoletoValidateRequestDTO request) {
        String linha = request.getLinhaDigitavel().trim();

        if (linha.length() != 47 && linha.length() != 48) {
            throw new IllegalArgumentException("Linha digitável inválida: deve conter 47 ou 48 dígitos.");
        }

        // Extrai informações básicas
        String codigoBanco = linha.substring(0, 3);
        String nomeBanco = obterNomeBanco(codigoBanco);
        double valor = extrairValor(linha);
        String documentBeneficiario = extrairDocumentDoBeneficiario(linha);
        String agenciaBeneficiario = extrairAgenciaBeneficiario(linha);

        // Busca ou cria beneficiário
        Beneficiario beneficiario = beneficiarioService.buscarOuCriarBeneficiario(
                documentBeneficiario,
                "Instituição bancária", // Nome padrão, será verificado depois
                nomeBanco,
                agenciaBeneficiario).orElse(null);

        // Verificações de segurança
        int qtdDenuncias = 0;
        boolean documentValido = false;
        boolean documentCompativel = false;
        boolean compECompativel = false;

        if (beneficiario != null) {
            qtdDenuncias = reportRepository.countByBeneficiario(beneficiario);

            // Verificar document com API do Banco Central
            documentValido = verificarDocumentNoBancoCentral(documentBeneficiario);

            // Verificar se document é compatível com nome (simulação)
            documentCompativel = verificarCompatibilidadeDocumentNome(documentBeneficiario, beneficiario.getNome());

            // Verificar compatibilidade COMPE com conta
            compECompativel = verificarCompatibilidadeCOMPE(codigoBanco, documentBeneficiario);
        }

        // Define status e recomendação baseado nas verificações
        String status = "válido";
        String recomendacao = "PAGAR";
        String motivo = null;

        if (nomeBanco.equals("Banco desconhecido")) {
            status = "suspeito";
            recomendacao = "NÃO PAGAR";
            motivo = "Banco não reconhecido.";
        } else if (qtdDenuncias > 0) {
            status = "suspeito";
            recomendacao = "NÃO PAGAR";
            motivo = "Beneficiário com " + qtdDenuncias + " denúncia(s) registrada(s).";
        } else if (!documentValido) {
            status = "suspeito";
            recomendacao = "NÃO PAGAR";
            motivo = "Documento do beneficiário inválido segundo Banco Central.";
        } else if (!documentCompativel) {
            status = "suspeito";
            recomendacao = "NÃO PAGAR";
            motivo = "Incompatibilidade entre documento e nome do beneficiário.";
        } else if (!compECompativel) {
            status = "suspeito";
            recomendacao = "NÃO PAGAR";
            motivo = "Incompatibilidade entre código do banco e conta do beneficiário.";
        }

        // Monta a resposta final
        BoletoResponseDTO response = new BoletoResponseDTO();
        response.setLinhaDigitavel(linha);
        response.setBanco(nomeBanco);
        response.setBeneficiarioNome(beneficiario != null ? beneficiario.getNome() : "Instituição bancária");
        response.setValor(valor);
        response.setDataValidacao(LocalDateTime.now());
        response.setStatusValidacao(status);
        response.setMensagem("Boleto processado com sucesso.");
        response.setMotivo(motivo);
        response.setRecomendacao(recomendacao);
        response.setDocumentBeneficiario(documentBeneficiario);

        return response;
    }

    private boolean verificarDocumentNoBancoCentral(String document) {
        try {
            if (!isValidCNPJ(document)) {
                return false;
            }

            // Brasil API - Gratuita e mais estável
            String url = "https://brasilapi.com.br/api/cnpj/v1/" + document;

            RestTemplate restTemplate = createRestTemplateWithTimeout();

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "OlhoNoBoleto-API/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false; // CNPJ não encontrado
            }

            if (response.getStatusCode() != HttpStatus.OK) {
                return false;
            }

            return parseBrasilAPIResponse(response.getBody());

        } catch (Exception e) {
            System.err.println("Erro na consulta CNPJ: " + e.getMessage());
            return false;
        }
    }

    private boolean isValidCNPJ(String document) {
        if (document == null || document.length() != 14 || !document.matches("\\d+")) {
            return false;
        }
        return true;
    }

    private RestTemplate createRestTemplateWithTimeout() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 segundos
        factory.setConnectionRequestTimeout(10000);
        factory.setReadTimeout(30000); // 30 segundos para APIs lentas

        return new RestTemplate(factory);
    }

    private boolean parseBrasilAPIResponse(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // Verificar situação
            String descricaoSituacao = root.path("descricao_situacao_cadastral").asText("");
            String situacao = root.path("situacao_cadastral").asText("");

            boolean isAtiva = "ATIVA".equalsIgnoreCase(descricaoSituacao) ||
                    "02".equals(situacao); // Código 2 = Ativa

            // Verificar dados básicos
            String razaoSocial = root.path("razao_social").asText("");
            boolean dadosValidos = !razaoSocial.isEmpty();

            return isAtiva && dadosValidos;

        } catch (Exception e) {
            System.err.println("Erro ao parsear resposta Brasil API: " + e.getMessage());
            return false;
        }
    }

    private boolean verificarCompatibilidadeDocumentNome(String document, String nome) {
        // Simulação - na implementação real, usar API de consulta de CNPJ/CPF
        // para verificar se o nome bate com o documento
        return true;
    }

    private boolean verificarCompatibilidadeCOMPE(String codigoBanco, String document) {
        // Verificar se o código COMPE é compatível com o banco do beneficiário
        // Esta é uma verificação simplificada
        return true;
    }

    private String extrairAgenciaBeneficiario(String linha) {
        // Extrair agência do beneficiário da linha digitável
        // Posição varia conforme o banco, esta é uma implementação genérica
        if (linha.length() >= 15) {
            return linha.substring(3, 7); // Exemplo genérico
        }
        return "0000";
    }

    private String obterNomeBanco(String codigoBanco) {
        Map<String, String> bancos = Map.ofEntries(
                // Bancos Tradicionais
                Map.entry("001", "Banco do Brasil"),
                Map.entry("033", "Santander"),
                Map.entry("104", "Caixa Econômica Federal"),
                Map.entry("237", "Bradesco"),
                Map.entry("341", "Itaú Unibanco"),
                Map.entry("356", "Banco Real"),
                Map.entry("389", "Banco Mercantil do Brasil"),
                Map.entry("399", "HSBC Bank Brasil"),
                Map.entry("422", "Banco Safra"),
                Map.entry("453", "Banco Rural"),
                Map.entry("633", "Banco Rendimento"),
                Map.entry("652", "Itaú Unibanco Holding"),
                Map.entry("745", "Banco Citibank"),

                // Bancos Digitais e Fintechs
                Map.entry("077", "Banco Inter"),
                Map.entry("212", "Banco Original"),
                Map.entry("260", "Nubank"),
                Map.entry("290", "PagSeguro"),
                Map.entry("323", "Mercado Pago"),
                Map.entry("332", "Acesso Soluções de Pagamento"),
                Map.entry("637", "Banco Sofisa"),
                Map.entry("653", "Banco Voiter"),
                Map.entry("655", "Banco Votorantim"),
                Map.entry("735", "Banco Neon"),
                Map.entry("085", "Cooperativa Central Ailos"),
                Map.entry("197", "Stone Pagamentos"),

                // Corretoras e Investimentos
                Map.entry("102", "XP Investimentos"),
                Map.entry("120", "Banco Rodobens"),
                Map.entry("184", "Banco Itaú BBA"),
                Map.entry("746", "Banco Modal"),

                // Cooperativas
                Map.entry("748", "Sicredi"),
                Map.entry("756", "Sicoob"));
        return bancos.getOrDefault(codigoBanco, "Banco desconhecido");
    }

 private double extrairValor(String linha) {
    String valorStr = linha.substring(linha.length() - 10);
    try {
        return Double.parseDouble(valorStr) / 100;
    } catch (NumberFormatException e) {
        return 0.0;
    }
}
    private String extrairDocumentDoBeneficiario(String linha) {
        // Mapeamento de códigos de bancos (COMPE) para CNPJ/CPF padrão
        // Bancos Tradicionais
        if (linha.startsWith("001"))
            return "00000000000191"; // Banco do Brasil
        if (linha.startsWith("033"))
            return "60701190000104"; // Santander
        if (linha.startsWith("104"))
            return "00360305000104"; // Caixa Econômica Federal
        if (linha.startsWith("237"))
            return "60746948000112"; // Bradesco
        if (linha.startsWith("341"))
            return "61532679000150"; // Itaú Unibanco
        if (linha.startsWith("356"))
            return "06271464000135"; // Banco Real (atualmente Santander)
        if (linha.startsWith("389"))
            return "17184037000110"; // Banco Mercantil do Brasil
        if (linha.startsWith("399"))
            return "04064547000187"; // HSBC Bank Brasil
        if (linha.startsWith("422"))
            return "58141356000182"; // Banco Safra
        if (linha.startsWith("453"))
            return "00416968000101"; // Banco Rural
        if (linha.startsWith("633"))
            return "68900810000141"; // Banco Rendimento
        if (linha.startsWith("652"))
            return "01522376000152"; // Itaú Unibanco Holding
        if (linha.startsWith("745"))
            return "33479023000120"; // Banco Citibank
        if (linha.startsWith("748"))
            return "33644115000190"; // Banco Cooperativo Sicredi
        if (linha.startsWith("756"))
            return "02038232000164"; // Bancoob (Sicoob)

        // Bancos Digitais e Fintechs
        if (linha.startsWith("077"))
            return "92875780000131"; // Banco Inter
        if (linha.startsWith("212"))
            return "92894922000143"; // Banco Original
        if (linha.startsWith("260"))
            return "09313766000194"; // Nu Pagamentos (Nubank)
        if (linha.startsWith("290"))
            return "08561701000109"; // PagSeguro
        if (linha.startsWith("323"))
            return "01027058000191"; // Mercado Pago
        if (linha.startsWith("332"))
            return "13140088000130"; // Acesso Soluções de Pagamento
        if (linha.startsWith("637"))
            return "60889128000148"; // Banco Sofisa
        if (linha.startsWith("653"))
            return "36498109000179"; // Banco Voiter (antigo Indusval)
        if (linha.startsWith("655"))
            return "62098988000186"; // Banco Votorantim
        if (linha.startsWith("735"))
            return "03801695000106"; // Banco Neon (via Banco Votorantim)
        if (linha.startsWith("085"))
            return "05442055000126"; // Cooperativa Central Ailos
        if (linha.startsWith("197"))
            return "24074692000191"; // Stone Pagamentos

        // Corretoras e Investimentos
        if (linha.startsWith("102"))
            return "02332886000104"; // XP Investimentos
        if (linha.startsWith("332"))
            return "03801695000106"; // Rico Investimentos (via XP)
        if (linha.startsWith("120"))
            return "01634601000140"; // Banco Rodobens
        if (linha.startsWith("184"))
            return "33657200000120"; // Banco Itaú BBA
        if (linha.startsWith("746"))
            return "61532679000150"; // Banco Modal (via Itaú)

        return "00000000000000"; // Padrão para bancos não mapeados
    }
}
