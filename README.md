# Digital Harmonic Field 🎵
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg?logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-green.svg?logo=android)](https://developer.android.com/jetpack/compose)
[![Platform](https://img.shields.io/badge/Platform-Android_Mobile_%26_Tablets-brightgreen.svg?logo=android)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Creator](https://img.shields.io/badge/Creator-JR__TECH__OFC-cyan.svg)](https://github.com/jr-tech-ofc)

O **Digital Harmonic Field** é um aplicativo Android nativo contemporâneo, desenvolvido em **Kotlin** e **Jetpack Compose**, sob as diretrizes do **Material Design 3**. O app foi construído de forma sob medida para instrumentistas, músicos de igreja e produtores que precisam de acesso visual e instantâneo a campos harmônicos em tempo real sem distrações.

Totalmente off-line, o aplicativo oferece um layout inteligente, de alto contraste visual e focado em navegação reativa extremamente rápida (máximo dois toques).

---

## 🎨 Logotipo e Identidade Visual Premium
O aplicativo apresenta um logotipo customizado programático e responsivo diretamente construído via Compose `Canvas` e em formato vetorial adaptativo:

* **Minimalist Fretboard Matrix:** Representação geométrica de um braço de guitarra ou contrabaixo com trastes prateados (`#94A3B8`) e azuis luminosos (`#3B82F6` / `#22D3EE`), com nós e círculos que representam acordes vivos.
* **Tipografia Futurista de Alto Impacto:** Títulos com espaçamentos marcantes de fontes contemporâneas que dão ao projeto um visual profissional de alto nível.

---

## 🧠 Psicologia das Cores e Sensações Musicais
Cada grau do campo harmônico não é apenas representado por números romanos ou cifras neutras. O app mapeia individualmente cada grau da escala cromática para sua correspondente **função harmônica e sensação acústica**, trazendo a psicologia das cores para expressar o sentimento gerado no cérebro do ouvinte:

| Grau | Termologia Musical | Sensação Psicoacústica | Cor Clave | Representação Visual |
| :---: | :--- | :--- | :---: | :--- |
| **I** | **Tônica** | Ponto de Chegada / Repouso Absoluto | **Verde Esmeralda** | `#10B981` (Terapeuticamente seguro e de descanso) |
| **II** | **Subdominante** | Afastamento / Início do Movimento | **Azul Céu** | `#3B82F6` (Intelecto, foco e direcionamento seguro) |
| **III** | **Tônica Mediana** | Transição Suave / Repouso Flutuante | **Verde Piscina/Teal** | `#2DD4BF` (Equilíbrio místico e flutuação suave) |
| **IV** | **Subdominante** | Abertura / Expansão | **Âmbar Luminoso** | `#F59E0B` (Luz solar, aconchego quente e abertura) |
| **V** | **Dominante** | Gatilho de Puxada / Tensão Máxima | **Vermelho Intenso** | `#EF4444` (Instabilidade urgente, chamego estressado) |
| **VI** | **Tônica Relativa** | Falsa Resolução / Desvio Emocional | **Roxo Real** | `#8B5CF6` (Magia, introspecção e desvio surpresa) |
| **VII** | **Dominante Simétrico** | Instabilidade Pura / Suspense | **Fúcsia Néon** | `#D946EF` (Mistério definitivo, tensão não comedida) |

Este mapeamento dinâmico é aplicado tanto a escalas **Maiores** quanto **Menores**, garantindo consistência visual-auditiva sem igual.

---

## 🏎️ Navegação Fluida e Comportamento Adaptativo (Anti-Scroll Móvel)

O aplicativo resolve de forma cirúrgica os desafios práticos de tocar ao vivo com o instrumento nas mãos:

* **Visualização Sem Toques:** No modo retrato (*Portrait*), a tela de acordes se expande verticalmente para leitura estável.
* **Modo Paisagem Otimizado (Landscape):** Ao rotacionar a tela para o modo horizontal, o aplicativo **oculta automaticamente o logotipo e as decorações da marca**, permitindo que a grade e os cards dos acordes preencham 100% da área útil disponível. Isso elimina a necessidade de rolagens acidentais de tela (*zero-scroll*) durante apresentações e cultos!
* **Acesso Instantâneo:** Sem telas lentas de carregamento (splash screens) ou menus aninhados complexos. O músico abre o aplicativo e, com **um único toque** na tonalidade desejada, todos os 7 graus harmônicos ideais são carregados na tela instantaneamente.

---

## 🛠️ Arquitetura e Tech Stack

O projeto segue padrões industriais de engenharia de software Android:

* **Linguagem:** [Kotlin 2.0.0](https://kotlinlang.org/) (Sintaxe moderna, nula e concisa).
* **Kit de UI:** [Jetpack Compose (Material Core 3)](https://developer.android.com/jetpack/compose) reativo.
* **Gerenciamento de Estado:** `ViewModel` emparelhado com `MutableStateFlow` para fluxos de dados unidirecionais seguros e reativos ao ciclo de vida da Activity (`collectAsStateWithLifecycle`).
* **Edge-to-Edge:** Implementado nativamente usando `enableEdgeToEdge()` do AndroidX, garantindo que o aplicativo flua de forma imersiva sob as barras de status e navegação do sistema sem cortes de barras.
* **Testabilidade:** Suíte de testes unitários integrados com **Robolectric** e screenshot-testing integrado via **Roborazzi** para garantir resiliência contra regressões na renderização visual.

---

## 📂 Organização Prática dos Arquivos
```text
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt        # Interfaces de UI com suporte inteligente a rotação responsiva
│   │   │   │   ├── HarmonicData.kt        # Banco de dados portátil com suporte dinâmico para os 24 tons
│   │   │   │   └── ui/theme/
│   │   │   │       ├── Theme.kt           # Centralização de paletas de alto contraste baseadas em Night-Mode
│   │   │   │       └── Color.kt           # Cores oficiais da matriz harmônica
│   │   │   └── res/
│   │   │       └── drawable/
│   │   │           ├── ic_launcher_background.xml
│   │   │           └── ic_launcher_foreground.xml # Ícone adaptativo desenhado programaticamente
│   │   └── test/
│   │       └── java/com/example/          # Suíte de testes automatizados com Robolectric
│   └── build.gradle.kts                   # Declaração das dependências e plugins atualizados
```

---

## 🚀 Como Executar e Testar o App

### Pré-requisitos
* **JDK 17** ou superior instalado no sistema operacional.
* **Android Studio Ladybug** (ou posterior) para deploy direto no seu dispositivo físico por Wi-Fi ou cabo USB.

### Compilação por Linha de Comando (Gradle)
No terminal, utilize os comandos do Gradle integrados na raiz do projeto (não use `./gradlew`, use `gradle` direto no nosso ambiente):

1. **Compilar e buildar aplicativo inteiro:**
   ```bash
   gradle compileDebugSources
   ```

2. **Gerar pacote APK executável de depuração (Debug APK):**
   ```bash
   gradle assembleDebug
   ```
   *O APK será instalado e disponibilizado automaticamente no seu diretório de outputs compilados.*

3. **Rodar todos os testes unitários do Robolectric:**
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 🤝 Como Contribuir

Este projeto é **100% open-source**! Se deseja adicionar novos recursos (como suporte a acordes com sétimas e nonas enriquecidas, bemóis equivalentes ou suporte a MIDI nativo), siga estes passos:

1. Faça um **Fork** do projeto.
2. Crie sua branch para a alteração desejada:
   ```bash
   git checkout -b feature/minha-melhoria
   ```
3. Realize o commit das suas modificações de código (mantenha o linter ativo):
   ```bash
   git commit -m "feat: adiciona suporte a acordes com sétima menor no campo"
   ```
4. Envie as alterações para o seu repositório remoto:
   ```bash
   git push origin feature/minha-melhoria
   ```
5. Abra um **Pull Request** detalhando sua alteração técnica.

---

## ⛪ Propósito e Idealização

O **Digital Harmonic Field** nasceu para resolver os conflitos visuais reais de transição harmônica em ensaios e momentos litúrgicos ao vivo. Idealizado para libertar músicos da dependência de pastas de cifras confusas, proporcionando velocidade mental em 2 segundos de foco visual.

Idealizado e mantido com carinho por **JR_TECH_OFC** &copy; 2026.
