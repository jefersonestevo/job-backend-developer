# Intelipost: Solução do Teste prático para Backend Developer

## Desafio 1

*Imagine que hoje tenhamos um sistema de login e perfis de usuários. O sistema conta com mais de 10 milhões de usuários, sendo que temos um acesso concorrente de cerca de 5 mil usuários. Hoje a tela inicial do sistema se encontra muito lenta. Nessa tela é feita uma consulta no banco de dados para pegar as informações do usuário e exibi-las de forma personalizada. Quando há um pico de logins simultâneos, o carregamento desta tela fica demasiadamente lento. Na sua visão, como poderíamos iniciar a busca pelo problema, e que tipo de melhoria poderia ser feita?*

## Solução Desafio 1:
 
A primeira ação a ser tomada é separar um ambiente com capacidade semelhante (se for igual melhor ainda) ao ambiente de produção.

Neste ambiente, a aplicação deve simular o mais próximo possível as condições de produção para realização de vários testes de performance.

O teste em si pode ser realizado com JMeter, para simular as 5000 requisições simultâneas e deve-se usar um profiler (JProfiler, por exemplo) para análise de quais as possíveis causas para a lentidão no sistema.

Com este profile, será possível pegar algum possível leak de memória/recursos, problemas relacionado ao GC, entre outros.

No geral, este tipo de problema tem algumas causas mais comuns e eu recomendaria analisá-las antes de quaisquer outras:

1) Não utilização de um pool de conexão no banco de dados

1) Dependendo da tecnologia utilizada para acesso ao banco (JPA, por exemplo), podem estar sendo realizadas queries desnecessárias no banco, então estas queries deveriam ser analisadas/reescritas.

1) Não utilização/existência de cache para dados que permitem este tipo de "persistência".

1) Analisar todas as queries executadas no banco de dados para verificar se há necessidade de criação de índices.

1) Analisar, caso a caso, a utilização de transações e isolation level utilizado no banco, pois em muito casos não é nem necessário a utilização de transações.

1) Analisar o uso de memória/cpu da aplicação para ver se ela não está no seu limite de processamento

## Desafio 2

*Com base no problema anterior, gostaríamos que você codificasse um novo sistema de login para muitos usuários simultâneos e carregamento da tela inicial. Lembre-se que é um sistema web então teremos conteúdo estático e dinâmico. Leve em consideração também que na empresa existe um outro sistema que também requisitará os dados dos usuários, portanto, este sistema deve expor as informações para este outro sistema de alguma maneira.*

## Solução Desafio 2:

### Premissas:

* Todos os itens anteriores foram efetuados e mesmo assim persistiu o problema de performance.
* Os dados de autenticação/autorização serão todos armazenados na base de dados
* Já existe uma base de dados relacional populada com 10 milhões de registros

### Solução Proposta:

Neste caso, será feita a utilização de uma base de dados NoSQL (MongoDB) para armazenamento de dados de informação do usuário. Estes dados permitem este tipo de armazenamento pois eles podem ser estruturados na forma de um "documento" com todas as informações mapeadas por um identificador único para cada usuário (o proprio ID do banco de dados relacional). Assim, esta aplicação diminui o nível de concorrência no banco de dados relacional e possui um banco de dados performático específico para este tipo de consultas.

Para esta solução NoSQL, o import de dados pode será feito de forma gradativa. Para tanto, será criada uma tabela de controle de import com uma unique key que identifica o id do usuário e, a cada consulta de usuário realizada, aplica-se o algoritmo:

1) Procura o usuário na base de dados NoSQL
1) Se não for encontrado, consulta ele na base de dados relacional e cadastra na base de dados NoSQL
1) Salva o registro na tabela de controle de carga para não fazer uma carga duplicada
1) Caso ocorra um erro de unique constraint neste insert, é um problema de concorrência e deve-se logar este evento e consultar o registro na base de dados NoSQL (Provavelmente um outro processo fez a mesma carga neste mesmo momento)
1) Retornar os dados para o usuário

Na próxima consulta, os dados já estarão cadastrados na base NoSQL e nada impede que seja criado um batch que faça este import em outros horários.

### Tecnologias Utilizadas:

#### Autenticação/Autorização
Os dados de login, senha e roles dos usuários serão armazenado no banco de dados relacional (PostgreSQL)

##### Acesso Web:
Para acesso Web, será feito um modelo de autentiação via HttpSession do Java. Será utilizado o "Spring Session Redis" para armazenar esta sessão em um redis externo à aplicação permitindo que uma sessão do usuário seja desacoplada da aplicação. Assim, a aplicação poderá ser escalada horizontalmente sem problemas.

##### Acesso API:
Para acesso via API, será utilizado um modelo de autentiação Stateless via JWT. Este modelo de autenticação permite que a aplicação cliente utilize um token JWT válido por um período de tempo grande. Como as aplicações clientes da API serão controladas internamente, fica mais simples para revogar este token.


#### Actuator:
A aplicação irá utilizar o Spring Boot Actuator para fornecer informações operacionais da aplicação através de URL's. As seguintes URl's estarão habilitadas:

* /actuator/health - Não é necessário autenticação
* /actuator/info - Não é necessário autenticação
* /actuator/threaddump - Autentiação Basic com um usuário que possua role "ACTUATOR"
* /actuator/metrics - Autentiação Basic com um usuário que possua role "ACTUATOR"

Isto permitirá, por exemplo, que o Kubernetes tenha uma URL (/actuator/health) para utilizar no readinessProbe e livenessProbe.

#### Swagger API:
A documentação da API do projeto pode ser obtido através da URI:
* /swagger-ui.html - Swagger UI no formato HTML
* /v2/api-docs - Json com os dados do Swagger

#### Testes unitários com Groovy e Spock:

A opção por utilizar testes unitários em Groovy com o framework Spock se dá pelo fato do Groovy ser uma linguagem menos verbosa que o Java e o Spock ser um framework que permite a criação de testes mais expressivos. O próprio modelo de testes do Spock já induz o desenvolvedor a criar uma documentação para os testes e torna eles muito mais simples de evoluir com o tempo

#### OpenTracing:
Esta aplicação será instrumentada utilizando a api do OpenTracing. A implementação real será o Jaeger. O OpenTracing permite fazer, de um modo não invasivo, a instrumentação de trace em um ambiente distribuído. Assim, ele nos dará maiores insumos para análise futura da utilização da aplicação.

### Executando o Projeto:

**// TODO**

