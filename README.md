### Introduksjon Akka

Tilhørende slides for workshop: http://arild.github.io/scala-workshop

Scala er et funksjonelt programmeringsspråk som kjøer på JVM'en

Futures & Promises er asynkrone primitiver for å uttrykke parallellitet.

Denne workshopen vil gi en introduksjon til Scala via noen slides og hands-on oppgaver, før Futures & Promises introduseres.

Før kommende workshop er det noen ting som bør være installert og satt opp:

1. Installerer Scala 2.10.x (http://www.scala-lang.org/downloads)
2. Installerer sbt 0.13.x (http://www.scala-sbt.org/0.13.0/docs/Getting-Started/Setup.html) - OBS Følg instruksjonene nøye - kan også være nødvendig å øke minne i filen sbt.bat ved å skrive inn følgende som ny linje (linje nr 13) :
set SBT_OPTS = -Xmx500M  -XX:MaxPermSize=128M
3. Installerer Scala-plugin i sitt IDE
       * Eclipse:
              http://scala-ide.org/download/current.html  
              Update sites er litt nede på siden. Velg site for Scala 2.10, og for den versjonen av Eclipse du bruker.  
              Alternativ: Last ned bundle-versjon med Eclipse og Scala plugin: http://scala-ide.org/download/sdk.html
       * IDEA IntelliJ:
              http://confluence.jetbrains.com/display/SCA/Scala+Plugin+for+IntelliJ+IDEA
4. Installerer git (http://git-scm.com/)
5. Hente ned prosjektet : `git clone https://github.com/arild/scala-workshop.git`
6. Eksekverer `sbt` fra mappen 'scala-workshop', og skriver enten `eclipse` eller `gen-idea` (avhengig av IDE)
7. Åpne prosjektet i Eclipse eller IntelliJ
8. Se at tester kjører: eksekverer `sbt test` fra mappen 'scala-workshop'.

Ved problemer med oppsettet, send en mail til arild.nilsen (at) bekk.no eller sjur.millidahl (at) bekk.no
