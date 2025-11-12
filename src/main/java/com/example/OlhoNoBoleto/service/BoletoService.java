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
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import com.example.OlhoNoBoleto.dto.boleto.BoletoResponseDTO;
import com.example.OlhoNoBoleto.dto.boleto.BoletoValidateRequestDTO;
import com.example.OlhoNoBoleto.exceptions.BusinessException;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.ReportRepository;

@Service

public class BoletoService {

    @Autowired
    private BeneficiarioService beneficiarioService;
    @Autowired
    private ReportRepository reportRepository;
    private static final Map<String, String> CODIGO_PARA_NOME_BANCO = Map.ofEntries(
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

    private static final Map<String, String> BANCO_PARA_CNPJ = Map.ofEntries(
            // Bancos Tradicionais
            Map.entry("001", "00000000000191"), // Banco do Brasil
            Map.entry("033", "60701190000104"), // Santander
            Map.entry("104", "00360305000104"), // Caixa Econômica Federal
            Map.entry("237", "60746948000112"), // Bradesco
            Map.entry("341", "61532679000150"), // Itaú Unibanco
            Map.entry("356", "06271464000135"), // Banco Real
            Map.entry("389", "17184037000110"), // Banco Mercantil do Brasil
            Map.entry("399", "04064547000187"), // HSBC Bank Brasil
            Map.entry("422", "58141356000182"), // Banco Safra
            Map.entry("453", "00416968000101"), // Banco Rural
            Map.entry("633", "68900810000141"), // Banco Rendimento
            Map.entry("652", "01522376000152"), // Itaú Unibanco Holding
            Map.entry("745", "33479023000120"), // Banco Citibank
            Map.entry("748", "33644115000190"), // Sicredi
            Map.entry("756", "02038232000164"), // Sicoob

            // Bancos Digitais e Fintechs
            Map.entry("077", "92875780000131"), // Banco Inter
            Map.entry("212", "92894922000143"), // Banco Original
            Map.entry("260", "09313766000194"), // Nubank
            Map.entry("290", "08561701000109"), // PagSeguro
            Map.entry("323", "01027058000191"), // Mercado Pago
            Map.entry("332", "13140088000130"), // Acesso Soluções de Pagamento
            Map.entry("637", "60889128000148"), // Banco Sofisa
            Map.entry("653", "36498109000179"), // Banco Voiter
            Map.entry("655", "62098988000186"), // Banco Votorantim
            Map.entry("735", "03801695000106"), // Banco Neon
            Map.entry("085", "05442055000126"), // Cooperativa Central Ailos
            Map.entry("197", "24074692000191"), // Stone Pagamentos

            // Corretoras e Investimentos
            Map.entry("102", "02332886000104"), // XP Investimentos
            Map.entry("120", "01634601000140"), // Banco Rodobens
            Map.entry("184", "33657200000120"), // Banco Itaú BBA
            Map.entry("746", "61532679000150") // Banco Modal
    );

    private String obterNomeBanco(String codigoBanco) {
        return CODIGO_PARA_NOME_BANCO.getOrDefault(codigoBanco, "Banco desconhecido");
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
        String codigoBanco = linha.substring(0, 3);
        return BANCO_PARA_CNPJ.getOrDefault(codigoBanco, "00000000000000");
    }

    private boolean verificarCompatibilidadeCOMPE(String codigoBanco, String document) {
        try {
            // Usando o mapeamento constante que já temos
            String cnpjEsperado = BANCO_PARA_CNPJ.get(codigoBanco);

            // Se não temos mapeamento para este banco, considerar compatível
            if (cnpjEsperado == null) {
                return true;
            }

            boolean isCompativel = document.equals(cnpjEsperado);

            System.out.println("Compatibilidade COMPE - Banco: " + codigoBanco +
                    ", CNPJ Informado: " + document +
                    ", CNPJ Esperado: " + cnpjEsperado +
                    ", Compatível: " + isCompativel);

            return isCompativel;

        } catch (Exception e) {
            System.err.println("Erro ao verificar compatibilidade COMPE: " + e.getMessage());
            return false;
        }
    }

    private String obterNomeRealBeneficiario(String document, String nomeBanco) {
        try {
            // Se é um banco conhecido, buscar nome da API
            if (BANCO_PARA_CNPJ.containsValue(document)) {
                String url = "https://brasilapi.com.br/api/cnpj/v1/" + document;

                RestTemplate restTemplate = createRestTemplateWithTimeout();
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode root = mapper.readTree(response.getBody());

                    String razaoSocial = root.path("razao_social").asText("");
                    String nomeFantasia = root.path("nome_fantasia").asText("");

                    // Preferir nome fantasia, depois razão social
                    if (!nomeFantasia.isEmpty()) {
                        return nomeFantasia;
                    } else if (!razaoSocial.isEmpty()) {
                        return razaoSocial;
                    }
                }
            }

            // Fallback: nome do banco + "S.A."
            return nomeBanco + " S.A.";

        } catch (Exception e) {
            // Fallback em caso de erro
            return nomeBanco + " S.A.";
        }
    }

    public BoletoResponseDTO validarBoleto(BoletoValidateRequestDTO request) {
        String linha = request.getLinhaDigitavel().trim();

        if (linha.length() != 47 && linha.length() != 48) {
            throw new IllegalArgumentException("Linha digitável inválida: deve conter 47 ou 48 dígitos.");
        }
        try {
            // Extrai informações básicas
            String codigoBanco = linha.substring(0, 3);
            String nomeBanco = obterNomeBanco(codigoBanco);
            double valor = extrairValor(linha);
            String documentBeneficiario = extrairDocumentDoBeneficiario(linha);
            String agenciaBeneficiario = extrairAgenciaBeneficiario(linha);
            String nomeRealBeneficiario = obterNomeRealBeneficiario(documentBeneficiario, nomeBanco);
            // Busca ou cria beneficiário
            Beneficiario beneficiario = beneficiarioService.buscarOuCriarBeneficiario(
                    documentBeneficiario,
                    nomeRealBeneficiario,
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
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Erro na validação do boleto: " + e.getMessage());
        }
    }

    public boolean verificarDocumentNoBancoCentral(String document) {
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

    private String extrairAgenciaBeneficiario(String linha) {
        if (linha == null || linha.length() < 15) {
            return "0000";
        }

        String codigoBanco = linha.substring(0, 3);

        // Para a maioria dos bancos brasileiros, a agência está entre posições 19-23
        // Isso é padrão na linha digitável de boletos
        if (BANCO_PARA_CNPJ.containsKey(codigoBanco)) {
            return linha.substring(19, 23); // Posição padrão
        }

        // Fallback para posição genérica
        return linha.substring(3, 7);
    }

    private boolean verificarCompatibilidadeDocumentNome(String document, String nomeBeneficiario) {
        try {
            if (nomeBeneficiario == null || nomeBeneficiario.trim().isEmpty()) {
                return false;
            }

            // Consultar Brasil API para pegar a razão social real
            String url = "https://brasilapi.com.br/api/cnpj/v1/" + document;

            RestTemplate restTemplate = createRestTemplateWithTimeout();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                return false;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            String razaoSocialReal = root.path("razao_social").asText("").toLowerCase().trim();
            String nomeFantasiaReal = root.path("nome_fantasia").asText("").toLowerCase().trim();
            String nomeInformado = nomeBeneficiario.toLowerCase().trim();

            // Verificar compatibilidade
            boolean compativel = razaoSocialReal.contains(nomeInformado) ||
                    nomeInformado.contains(razaoSocialReal) ||
                    (!nomeFantasiaReal.isEmpty() &&
                            (nomeFantasiaReal.contains(nomeInformado) ||
                                    nomeInformado.contains(nomeFantasiaReal)));

            System.out.println("Compatibilidade nome - Razão Social: " + razaoSocialReal +
                    ", Informado: " + nomeInformado +
                    ", Compatível: " + compativel);

            return compativel;

        } catch (Exception e) {
            System.err.println("Erro ao verificar compatibilidade nome/documento: " + e.getMessage());
            return false;
        }
    }

}
