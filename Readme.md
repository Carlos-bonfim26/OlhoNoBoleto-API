# OlhoNoBoleto API
![spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![junit](https://img.shields.io/badge/Junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![insomia](https://img.shields.io/badge/Insomnia-5849be?style=for-the-badge&logo=Insomnia&logoColor=white)
## Objetivo

Resolver o problema de boleto falso, QR Code que é na verdade Pix para golpista. 

## O que fazemos 

o usuário (ou app do banco/loja) envia os dados do boleto (linha digitável) ou o QR Code; a API decodifica, consulta uma base (ou serviço simulado) para validar o beneficiário, banco destinatário e sinaliza discrepâncias (nome do beneficiário diferente do banco, banco não confere com convenção). Oferece recomendação “PAGAR / NÃO PAGAR” e instruções.