1. Visão Geral do Projeto

Este projeto de gerenciamento de restaurantes é composto por microserviços que permitem o gerenciamento de mesas, reservas, restaurantes e pedidos. Além disso, um serviço de envio de email quando uma reserva é efetuada.A arquitetura é orientada a eventos, o que permite que os serviços se comuniquem de forma assíncrona e escalável.

2. Arquitetura do Sistema

O sistema é baseado em uma arquitetura de microserviços orientada a eventos. Cada microserviço é responsável por um conjunto específico de funcionalidades e se comunica com outros serviços por meio de mensagens enviadas via RabbitMQ. Isso permite que os serviços operem de forma desacoplada, respondendo a eventos específicos, como a criação de uma mesa ou reserva.

3. Tecnologias Utilizadas

Spring Boot: Framework principal utilizado para construir os microsserviços.

RabbitMQ: Broker de mensagens utilizado para comunicação entre os microsserviços.

Docker: Ferramenta de conteinerização utilizada para empacotar os microsserviços.
Docker Compose: Ferramenta para definir e gerenciar multi-containers Docker, permitindo a orquestração dos micro serviços e do RabbitMQ.

4. Configuração do Ambiente

Docker Compose:
Configura e orquestra os containers de todos os micro serviços e do RabbitMQ.

Docker:
Todos os serviços estão configurados para usar a mesma rede Docker, permitindo que se comuniquem entre si usando seus nomes de serviço.

5. Design Orientado a Eventos

No design orientado a eventos, os microserviços se comunicam emitindo e escutando eventos através do RabbitMQ. Cada microserviço é responsável por escutar eventos específicos que lhe dizem respeito e agir conforme necessário.
