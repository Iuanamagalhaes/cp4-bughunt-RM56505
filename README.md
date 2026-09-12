# Checkpoint 4 — Bug Hunt StreamFIAP

## Identificação

| Integrante | RM | Turma |
|---|---|---|
| **Luana Magalhães Freire** | **565305** | **2CCPH** |

| Campo | |
|---|---|
| **Total de bugs corrigidos** | **12 / 12** |
| **Total de ajustes de Clean Code** | **7 / 6** |

---

## Parte 1 — Bugs encontrados

> Uma linha por bug, na ordem em que você os encontrou. Use a numeração dos seus
> commits (`fix: bug01 ...`). Preencha TODAS as colunas — metade da nota está aqui.

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| **bug01** | GET /api/conteudos/{id} em um id inexistente retornou corpo vazio com status 200 em vez de erro | `ConteudoController.buscarPorId()`, o **catch (Exception e)** captura a `ConteudoNaoEncontradoException` e não faz nada com ela (possuia apenas um comentário "TODO: tratar isso depois"), retornando **null** | Removi o **try/catch** e deixei a exceção seguir até o `GlobalExceptionHandler`, que já tinha um tratamento pronto para `ConteudoNaoEncontradoException` | **Tratamento de exceções** / propagação de erros (não capturar exceção sem tratá-la) |
| **bug02** | GET /api/conteudos/categoria/{categoria} e a lista voltou vazia mesmo com filmes de categorias cadastradas | `ConteudoController.listarPorCategoria()`, comparação **`c.getCategoria() == categoria`** estava comparando a referência das Strings, não o conteúdo da String | Substituí a busca manual pelo método **`findByCategoria()`** que já existia no `ConteudoRepository` | **Comparação de objetos em Java** |
| **bug03** | Série cadastrada voltava com título, categoria, duração e classificação **nulos** | Construtor `Serie(String, String, int, int, int)` **não chama `super`** | Adicionei **`super(titulo, categoria, duracaoMinutos, classificacaoEtaria, true)`** no início do construtor | **Herança** e encadeamento de construtores com `super()` |
| **bug04** | O preço de uma série com mais de uma temporada veio no preço padrão (R$ 9,90), em vez de ajustado | `Serie.calcularPrecoAluguel(double desconto)`, assinatura diferente do método da superclasse (**sobrecarga, não sobrescreve**, então nunca é chamado) | Corrigi a assinatura para **`@Override public double calcularPrecoAluguel()`** sem parâmetro | **Polimorfismo**, sobrescrita (**@Override**) e sobrecarga de métodos |
| **bug05** | GET /{id}/preco-promocional de um filme e o preço promocional veio **maior** que o preço normal | `Filme.aplicarPromocao(double preco)`, aumenta 20% em vez de aplicar desconto | Corrigi para **`return preco * 0.8`** (desconto de 20%, conforme contrato e a documentação da interface `Promocionavel`) | **Interfaces** e contrato de comportamento ("deve aplicar 20% de desconto") |
| **bug06** | O usuário era cadastrado com um nome, mas o campo nome retornava como **null** | No construtor, **`nome = nome`** atribuía o parâmetro a ele mesmo, sem salvar o valor no campo da classe | Corrigi para **`this.nome = nome`**, fazendo com que o nome informado fosse salvo corretamente no campo da classe | O parâmetro e o atributo tinham o mesmo nome, causando confusão na hora de identificar qual variável estava sendo usada (**shadowing**) |
| **bug07** | O usuário conseguia realizar um aluguel mesmo sem ter créditos suficientes, ficando com o saldo negativo | O método `temCreditosSuficientes()` comparava os valores de forma **invertida**, fazendo com que a verificação de créditos suficientes retornasse um resultado incorreto | Corrigi para **`return this.creditos >= preco`** | **Lógica booleana** e comparação de valores |
| **bug08** | Um conteúdo marcado como **indisponível** ainda conseguia ser alugado normalmente | `Usuario.alugar(Conteudo conteudo)`, não verificava se o conteúdo estava disponível antes de realizar o aluguel, mesmo já existindo uma exceção para esse caso | Adicionei uma verificação no início do método para impedir o aluguel de conteúdos que não estão disponíveis (**`if (!conteudo.isDisponivel()) throw new ConteudoIndisponivelException(...)`**) | **Validação de regra de negócio** e uso de **exceção customizada** |
| **bug09** | Ao cadastrar um usuário, o campo **id** retornava como **null** tanto na resposta da API quanto no banco de dados | `Usuario`, campo id anotado apenas com `@Id`, sem nenhuma configuração para gerar o identificador automaticamente | Adicionei **`@GeneratedValue(strategy = GenerationType.IDENTITY)`** acima do `@Id` para que o identificador seja gerado automaticamente | Geração automática de chave primária (**JPA** / mapeamento objeto-relacional) |
| **bug10** | Era possível cadastrar filmes, séries e documentários com **duracaoMinutos** igual a 0 ou com valor negativo | Os endpoints de cadastro em `ConteudoController` não verificavam se a duração informada era válida | Criei a exceção **`ConteudoInvalidoException`**, adicionei o tratamento para retornar **400 Bad Request** e incluí a validação de duração nos três endpoints de cadastro | **Validação de dados de entrada** |
| **bug11** | Ao tentar alugar um conteúdo com classificação etária incompatível, a API retornava um erro **500** genérico em vez de explicar o motivo | A `ClassificacaoIndicativaException` não tinha um tratamento específico no `GlobalExceptionHandler` | Adicionei um **`@ExceptionHandler`** para `ClassificacaoIndicativaException`, retornando **403 Forbidden** com a mensagem da exceção | **Tratamento de exceções** / `@RestControllerAdvice` |
| **bug12** | GET /api/conteudos/{id}/preco-promocional de um documentário retornava o preço padrão em vez de ser **gratuito** | `Documentario` não sobrescrevia o método `calcularPrecoAluguel()`, então herdava o comportamento padrão da classe `Conteudo` | Adicionei a sobrescrita de **`calcularPrecoAluguel()`** em `Documentario`, retornando **0.0** | **Herança** e sobrescrita de métodos (**@Override**) |

---

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| **clean01** | `ConteudoController`, bloco comentado | **Código comentado** não deve ficar no repositório, o histórico já é mantido pelo Git | Removi o bloco comentado |
| **clean02** | `ConteudoController.calcularDescontoAntigo(double preco)`, método privado que nunca é chamado em nenhum lugar do código | **Código morto** não deve permanecer no projeto | Removi o método inteiro |
| **clean03** | `Usuario.alugar()`, parâmetro **c** e variável local **p** com nomes pouco descritivos, que não deixavam claro o que cada um representava no método | Nomes de variáveis devem ser **significativos** e deixar claro o que representam | Renomeei **p** para **preco** e **c** para **conteudo** |
| **clean04** | `Usuario.debitarCreditos()`, comentário informando que o valor era adicionado aos créditos, quando o código na verdade fazia uma **subtração** | Comentário não condiz com o que o código realmente faz | Corrigi para: *debita (subtrai) o valor dos créditos do usuário* |
| **clean05** | `Usuario.alugar()`, vários **System.out.println()** responsáveis por montar e exibir o recibo dentro do método de aluguel | Viola o princípio da **SRP (responsabilidade única)**, pois a Model `Usuario` estava assumindo também a responsabilidade de exibir o recibo ao usuário | Removi as impressões da model |
| **clean06** | Valores fixos estavam espalhados pelas classes `Conteudo`, `Filme` e `Serie` | Números fixos usados diretamente nos cálculos, sem nomes que deixassem claro o que cada valor representava (**magic numbers**) | Substituí os números fixos por **constantes** com nomes que deixam claro o que cada valor representa |
| **clean07** | `Conteudo.duracaoMinutos` | O atributo era o único da classe que estava como **public**, permitindo que outras classes acessassem seu valor diretamente | Mudei `duracaoMinutos` para **private** e atualizei os controllers para acessar os atributos usando seus **getters**, como `getCategoria()` e `getDuracaoMinutos()`, em vez de acessar os campos diretamente |

---

## Parte 3 — Perguntas de reflexão

### Injeção de dependência (Aula 13)

> *Os controllers recebem os repositories via `@Autowired` (ex.: `ConteudoController` usa `ConteudoRepository`). Explique por que o Spring precisa gerenciar esses objetos em vez de criarmos com `new ConteudoRepository()`. O que exatamente o Spring faz ao injetar um bean, e por que isso não funcionaria com um `new` comum?*

O Spring gerencia os repositories porque `ConteudoRepository` é uma **interface**, não é possível instanciá-la com `new`. O Spring Data JPA cria automaticamente a implementação (**bean**) e a **injeta** via `@Autowired`, cuidando de toda a integração com o banco e o ciclo de vida do objeto, algo que um `new` comum não faria.

---

### JDBC vs Spring Data JPA (Aulas 12 e 13)

> *Na Aula 12 escrevemos um `ProdutoDAO` na mão com `Connection`, `PreparedStatement` e `ResultSet`. Aqui o `ConteudoRepository` tem 2 linhas e faz CRUD completo. Compare as duas abordagens: o que o Spring Data JPA automatiza, o que o JDBC/DAO ainda resolve melhor, e como o `findByCategoria` consegue funcionar sem implementação.*

No **JDBC** tudo é manual (conexão, SQL, `PreparedStatement`, `ResultSet`). O **Spring Data JPA** automatiza o CRUD e interpreta o nome do método (`findBy` + campo) para gerar a consulta sozinho — por isso `findByCategoria` funciona sem implementação. O JDBC/DAO ainda é melhor para consultas muito específicas ou controle fino sobre o acesso ao banco.

---

### Exceções checked vs unchecked (Aula 11)

> *A `ClassificacaoIndicativaException` estourava como um erro genérico do servidor, sem mensagem útil para o cliente. Explique a diferença entre `extends Exception` e `extends RuntimeException` no contexto desse bug, e como você fez a mensagem da regra (classificação indicativa) chegar de forma clara ao cliente da API.*

`extends Exception` gera uma **checked exception** (precisa ser tratada/declarada); `extends RuntimeException` gera uma **unchecked exception**. A `ClassificacaoIndicativaException` era checked, mas sem tratamento no `GlobalExceptionHandler`, virando erro 500. A solução foi criar um `@ExceptionHandler` específico, retornando **403 Forbidden** com a mensagem da regra violada.

---

### Sobrescrita vs sobrecarga (Aula 7)

> *Um dos bugs compilava sem nenhum erro: o método da `Serie` parecia sobrescrever `calcularPrecoAluguel`, mas na verdade sobrecarregava. Explique a diferença entre override e overload nesse caso e por que a anotação `@Override` teria impedido o bug.*

A sobrescrita (override) acontece quando uma classe filha redefine um método que já existe na classe pai, mantendo a mesma assinatura. Já a sobrecarga (overload) acontece quando existem métodos com o mesmo nome, mas com parâmetros diferentes. No bug da Serie, o método tinha o nome calcularPrecoAluguel, mas recebia um parâmetro:

```java
calcularPrecoAluguel(double desconto)
```

Enquanto o método da classe Conteudo não recebia nenhum parâmetro:

```java
calcularPrecoAluguel()
```

Por terem assinaturas diferentes, o método da Serie não estava sobrescrevendo o método da classe pai. Na prática, ele era apenas outro método com o mesmo nome, e o sistema continuava utilizando o método herdado de Conteudo, que retornava o preço padrão. Se @Override tivesse sido usado desde o início, o Java teria identificado que aquele método não correspondia a nenhum método da classe pai e o erro teria sido percebido antes.

---

### Onde blindar o objeto? (Aulas 3, 4 e 13)

> *Vimos bugs de dados inválidos aceitos (duração negativa, créditos negativos, campos nulos). Em quais lugares (construtor, setter, método do model) cada tipo de validação deve ficar? Justifique usando os bugs que você encontrou e explique por que validar só em um lugar não foi suficiente.*

O **construtor** garante que o objeto já nasça válido; os **setters** validam alterações posteriores; regras de **comportamento** (como aluguel de conteúdo indisponível ou sem créditos) ficam nos métodos do model; e a **validação de entrada da API** impede dados inválidos (ex.: duração negativa). Validar em um único lugar não basta porque os dados podem entrar ou mudar de formas diferentes — as camadas se complementam.

---

### Abstração e interface (Aulas 8 e 9)

> *`Conteudo` é abstrata e `Promocionavel` é uma interface. Explique a diferença de propósito entre as duas nesse projeto e o que mudaria no código se o Documentário passasse a ter promoções — quais classes/linhas seriam tocadas e quais ficariam intactas? O que isso diz sobre o design do sistema?*

`Conteudo` (**classe abstrata**) reúne o que é comum a filmes, séries e documentários; `Promocionavel` (**interface**) define um comportamento específico (poder ter promoção). Se `Documentario` passasse a ter promoções, só ele precisaria implementar `Promocionavel` e o método `aplicarPromocao()` — as demais classes ficariam **intactas**, o que mostra um design flexível e de fácil manutenção.
