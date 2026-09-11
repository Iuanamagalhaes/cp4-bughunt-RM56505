# Checkpoint 4 — Bug Hunt StreamFIAP

## Identificação

| Integrante | RM | Turma |
|---|---|---|
|Luana Magalhães Freire|565305|2CCPH|

| Campo | |
|---|---|
| **Total de bugs corrigidos** | ___ / 12 |
| **Total de ajustes de Clean Code** | ___ / 6 |

---

## Parte 1 — Bugs encontrados

> Uma linha por bug, na ordem em que você os encontrou. Use a numeração dos seus
> commits (`fix: bug01 ...`). Preencha TODAS as colunas — metade da nota está aqui.

| # | Sintoma observado (o que fiz/vi) | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | GET /api/conteudos/{id} em um id inexistente retornou corpo vazio com status 200 em vez de erro | ConteudoController.buscarPorId(), o catch (Exception e) captura a ConteudoNaoEncontradoException e não faz nada com ela (possuia apenas um comentário "TODO: tratar isso depois"), retornando null | Removi o try/catch e deixei a exceção seguir até o GlobalExceptionHandler, que já tinha um tratamento pronto para ConteudoNaoEncontradoException | Tratamento de exceções / propagação de erros (não capturar exceção sem tratá-la) |
| bug02 | GET /api/conteudos/categoria/{categoria} e a lista voltou vazia mesmo com filmes de categorias cadastradas | ConteudoController.listarPorCategoria(), comparação c.getCategoria() == categoria estava comparando a referência das Strings, não o conteúdo da String | Troquei para c.getCategoria().equalsIgnoreCase(categoria) | Comparação de objetos em Java |
| bug03 | Série cadastrada voltava com título, categoria, duração e classificação nulos | Construtor Serie(String, String, int, int, int) não chama super | Adicionei super(titulo, categoria, duracaoMinutos, classificacaoEtaria, true) no início do construtor | Herança e encadeamento de construtores com super() |
| bug04 | O preço de uma série com mais de uma temporada veio no preço padrão (R$ 9,90), em vez de ajustado | Serie.calcularPrecoAluguel(double desconto), assinatura diferente do método da superclasse (sobrecarga, não sobrescreve, então nunca é chamado) | Corrigi a assinatura para @Override public double calcularPrecoAluguel() sem parâmetro | Polimorfismo, sobrescrita (@Override) e sobrecarga de métodos |
| bug05 | GET /{id}/preco-promocional de um filme e o preço promocional veio maior que o preço normal | Filme.aplicarPromocao(double preco), aumenta 20% em vez de aplicar desconto | Corrigi para return preco * 0.8 (desconto de 20%, conforme contrato e a documentação da interface Promocionavel) | Interfaces e contrato de comportamento ("deve aplicar 20% de desconto") |
| bug06 | | | | |
| bug07 | | | | |
| bug08 | | | | |
| bug09 | | | | |
| bug10 | | | | |
| bug11 | | | | |
| bug12 | | | | |

## Parte 2 — Ajustes de Clean Code

| # | Onde estava | Qual princípio/boas práticas era violado | O que eu mudei |
|---|---|---|---|
| clean01 | ConteudoController, bloco comentado | Código comentado não deve ficar no repositório, o histórico já é mantido pelo Git | Removi o bloco comentado |
| clean02 | ConteudoController.calcularDescontoAntigo(double preco), método privado que nunca é chamado em nenhum lugar do código | Código morto não deve permanecer no projeto | Removi o método inteiro |
| clean03 |  Usuario.alugar(), parâmetro c e variável local p com nomes pouco descritivos, que não deixavam claro o que cada um representava no método | Nomes de variáveis devem ser significativos e deixar claro o que representam | Renomeei p para preco e c para conteudo |
| clean04 | Usuario.debitarCreditos(), comentário informando que o valor era adicionado aos créditos, quando o código na verdade fazia uma subtração | Comentário não condiz com o que o código realmente faz | Corrigi para: debita (subtrai) o valor dos créditos do usuário |
| clean05 | Usuario.alugar(), vários System.out.println() responsáveis por montar e exibir o recibo dentro do método de aluguel | Viola o princípio da SRP (responsabilidade única), pois a Model Usuario estava assumindo também a responsabilidade de exibir o recibo ao usuário | Removi as impressões da model |
| clean06 | | | |

---

## Parte 3 — Perguntas de reflexão

### 1. Injeção de dependência (Aula 13)
Os controllers recebem os repositories via `@Autowired` (ex.: `ConteudoController`
usa `ConteudoRepository`). Explique por que o Spring precisa gerenciar esses objetos
em vez de criarmos com `new ConteudoRepository()`. O que exatamente o Spring faz ao
injetar um bean, e por que isso não funcionaria com um `new` comum?

### 2. JDBC vs Spring Data JPA (Aulas 12 e 13)
Na Aula 12 escrevemos um `ProdutoDAO` na mão com `Connection`, `PreparedStatement` e
`ResultSet`. Aqui o `ConteudoRepository` tem 2 linhas e faz CRUD completo. Compare as
duas abordagens: o que o Spring Data JPA automatiza, o que o JDBC/DAO ainda resolve
melhor, e como o `findByCategoria` consegue funcionar sem implementação.

### 3. Exceções checked vs unchecked (Aula 11)
A `ClassificacaoIndicativaException` estourava como um erro genérico do servidor,
sem mensagem útil para o cliente. Explique a diferença entre `extends Exception` e
`extends RuntimeException` no contexto desse bug, e como você fez a mensagem da
regra (classificação indicativa) chegar de forma clara ao cliente da API.

### 4. Sobrescrita vs sobrecarga (Aula 7)
Um dos bugs compilava sem nenhum erro: o método da `Serie` parecia sobrescrever
`calcularPrecoAluguel`, mas na verdade sobrecarregava. Explique a diferença entre
override e overload nesse caso e por que a anotação `@Override` teria impedido o bug.

### 5. Onde blindar o objeto? (Aulas 3, 4 e 13)
Vimos bugs de dados inválidos aceitos (duração negativa, créditos negativos, campos
nulos). Em quais lugares (construtor, setter, método do model) cada tipo de validação
deve ficar? Justifique usando os bugs que você encontrou e explique por que validar só
em um lugar não foi suficiente.

### 6. Abstração e interface (Aulas 8 e 9)
`Conteudo` é abstrata e `Promocionavel` é uma interface. Explique a diferença de
propósito entre as duas nesse projeto e o que mudaria no código se o Documentário
passasse a ter promoções — quais classes/linhas seriam tocadas e quais ficariam
intactas? O que isso diz sobre o design do sistema?

---

## Parte 4 — Espaço livre (opcional)
