package com.example.OlhoNoBoleto.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.OlhoNoBoleto.model.Beneficiario;
import com.example.OlhoNoBoleto.repository.BeneficiarioRepository;

@Service
public class BeneficiarioService {

    @Autowired
    private BeneficiarioRepository beneficiarioRepository;

    public List<Beneficiario> buscarPorCnpjCpf(String cnpjCpf) {
        return beneficiarioRepository.findByDocument(cnpjCpf);
    }

    public Optional<Beneficiario> buscarOuCriarBeneficiario(String document, String nome, String banco, String agencia) {
        Optional<Beneficiario> beneficiarioExistente = beneficiarioRepository.findByDocument(document).stream().findFirst();
        
        if (beneficiarioExistente.isPresent()) {
            return beneficiarioExistente;
        } else {
            Beneficiario novoBeneficiario = new Beneficiario();
            novoBeneficiario.setDocument(document);
            novoBeneficiario.setNome(nome);
            novoBeneficiario.setBanco(banco);
            novoBeneficiario.setAgencia(agencia);
            novoBeneficiario.setTotalQueixas(0);
            
            beneficiarioRepository.save(novoBeneficiario);
            return Optional.of(novoBeneficiario);
        }
    }
}