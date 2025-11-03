package com.example.OlhoNoBoleto.service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.OlhoNoBoleto.dto.boleto.BoletoResponseDTO;
import com.example.OlhoNoBoleto.dto.boleto.BoletoValidateRequestDTO;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;

@Service
public class BoletoService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    public BoletoResponseDTO validarBoleto(BoletoValidateRequestDTO request) {

        String linha = request.getLinhaDigitavel().trim();

        // Valida tamanho
        if (linha.length() != 47 && linha.length() != 48) {
            throw new IllegalArgumentException("Linha digitável inválida: deve ter 47 ou 48 dígitos.");
        }

        // Extrai o código do banco (3 primeiros dígitos)
        String codigoBanco = linha.substring(0, 3);

        // Simula o valor extraído (nas posições certas)
        double valor = extrairValor(linha);

        // Simula o CNPJ/CPF do beneficiário conforme regras do projeto
        String cnpjCpfBeneficiario = extrairCnpjCpfDoBeneficiario(linha);
        // Busca beneficiário (se existir)
        List<Beneficiario> beneficiarios = beneficiarioRepository.findByCnpjCpf(cnpjCpfBeneficiario);
        Beneficiario beneficiario = (beneficiarios == null || beneficiarios.isEmpty()) ? null : beneficiarios.get(0);

        // Monta a resposta
        // Monta a resposta
        BoletoResponseDTO response = new BoletoResponseDTO();
        response.setLinhaDigitavel(linha);
        response.setBanco(codigoBanco);
        response.setBeneficiarioNome(beneficiario != null ? beneficiario.getNome() : "Desconhecido");
        response.setValor(valor);
        response.setDataValidacao(LocalDateTime.now());
        response.setStatusValidacao("válido");
        response.setMensagem("Boleto processado com sucesso.");

        return response;
    }

    // Métodos auxiliares simulando a lógica de extração
    private double extrairValor(String linha) {
        // Exemplo: últimos 10 dígitos = valor
        String valorStr = linha.substring(linha.length() - 10);
        return Double.parseDouble(valorStr) / 100;
    }

   private String extrairCnpjCpfDoBeneficiario(String linha) {
    // Mapeamento de códigos de bancos (COMPE) para CNPJ/CPF padrão
    // Bancos Tradicionais
    if (linha.startsWith("001")) return "00000000000191"; // Banco do Brasil
    if (linha.startsWith("033")) return "60701190000104"; // Santander
    if (linha.startsWith("104")) return "00360305000104"; // Caixa Econômica Federal
    if (linha.startsWith("237")) return "60746948000112"; // Bradesco
    if (linha.startsWith("341")) return "61532679000150"; // Itaú Unibanco
    if (linha.startsWith("356")) return "06271464000135"; // Banco Real (atualmente Santander)
    if (linha.startsWith("389")) return "17184037000110"; // Banco Mercantil do Brasil
    if (linha.startsWith("399")) return "04064547000187"; // HSBC Bank Brasil
    if (linha.startsWith("422")) return "58141356000182"; // Banco Safra
    if (linha.startsWith("453")) return "00416968000101"; // Banco Rural
    if (linha.startsWith("633")) return "68900810000141"; // Banco Rendimento
    if (linha.startsWith("652")) return "01522376000152"; // Itaú Unibanco Holding
    if (linha.startsWith("745")) return "33479023000120"; // Banco Citibank
    if (linha.startsWith("748")) return "33644115000190"; // Banco Cooperativo Sicredi
    if (linha.startsWith("756")) return "02038232000164"; // Bancoob (Sicoob)
    
    // Bancos Digitais e Fintechs
    if (linha.startsWith("077")) return "92875780000131"; // Banco Inter
    if (linha.startsWith("212")) return "92894922000143"; // Banco Original
    if (linha.startsWith("260")) return "09313766000194"; // Nubank
    if (linha.startsWith("290")) return "08561701000109"; // PagSeguro
    if (linha.startsWith("323")) return "01027058000191"; // Mercado Pago
    if (linha.startsWith("332")) return "13140088000130"; // Acesso Soluções de Pagamento
    if (linha.startsWith("637")) return "60889128000148"; // Banco Sofisa
    if (linha.startsWith("653")) return "36498109000179"; // Banco Voiter (antigo Indusval)
    if (linha.startsWith("655")) return "62098988000186"; // Banco Votorantim
    if (linha.startsWith("735")) return "03801695000106"; // Banco Neon (via Banco Votorantim)
    if (linha.startsWith("085")) return "05442055000126"; // Cooperativa Central Ailos
    if (linha.startsWith("197")) return "24074692000191"; // Stone Pagamentos
    
    // Corretoras e Investimentos
    if (linha.startsWith("102")) return "02332886000104"; // XP Investimentos
    if (linha.startsWith("332")) return "03801695000106"; // Rico Investimentos (via XP)
    if (linha.startsWith("120")) return "01634601000140"; // Banco Rodobens
    if (linha.startsWith("184")) return "33657200000120"; // Banco Itaú BBA
    if (linha.startsWith("746")) return "61532679000150"; // Banco Modal (via Itaú)
    
    return "00000000000000"; // Padrão para bancos não mapeados
}
}
