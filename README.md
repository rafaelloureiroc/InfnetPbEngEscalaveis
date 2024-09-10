Documentação Técnica:

1. Visão Geral do Projeto

- Este projeto de gerenciamento de restaurantes é composto por microsserviços que permitem o gerenciamento de mesas, reservas, restaurantes e pedidos. Além disso, um serviço de envio de email é acionado quando uma reserva é feita. A arquitetura é orientada a eventos, o que permite que os serviços se comuniquem de forma assíncrona e escalável.

2. Arquitetura do Sistema

- O sistema segue uma arquitetura orientada a eventos, onde cada micro serviço é responsável por uma funcionalidade específica e se comunica com outros serviços de forma desacoplada através de eventos. A comunicação entre os serviços é feita por meio do RabbitMQ, que atua como intermediário no envio e recebimento de mensagens. Isso permite que os microsserviços possam operar independentemente, respondendo a eventos

3. Tecnologias Utilizadas

- Spring Boot: Framework principal utilizado para construir os microsserviços.

- RabbitMQ: Broker de mensagens utilizado para comunicação entre os microsserviços.

- Docker: Ferramenta de conteinerização utilizada para empacotar os microsserviços.

- Docker Compose: Ferramenta para definir e gerenciar multi-containers Docker, permitindo a orquestração dos micro serviços e do RabbitMQ.
 
- Kubernetes: Plataforma que automatiza o deploy, gerenciamento e escalabilidade de aplicativos em contêineres. 


4. Configuração do Ambiente

Docker Compose:

- Configura e orquestra os containers de todos os micro serviços e do RabbitMQ.

Docker:

- Todos os serviços estão configurados para usar a mesma rede Docker, permitindo que se comuniquem entre si usando seus nomes de serviço.

Kubernetes: 

- Todos os serviços foram deployados utilizando kubernetes e funcionam sem erros, comunicando entre si utilizando os nomes dos respectivos serviços
