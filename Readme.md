# OlhoNoBoleto API
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

## Objetivo

Resolver o problema de boleto falso, QR Code que é na verdade Pix para golpista. 

## O que fazemos 

o usuário (ou app do banco/loja) envia os dados do boleto (linha digitável) ou o QR Code; a API decodifica, consulta uma base (ou serviço simulado) para validar o beneficiário, banco destinatário e sinaliza discrepâncias (nome do beneficiário diferente do banco, banco não confere com convenção). Oferece recomendação “PAGAR / NÃO PAGAR” e instruções.