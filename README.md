### Introduksjon Akka

Denne workshopen vil gi en introduksjon til Akka via slides og hands-on oppgaver. Workshopen benytter Scala.

Før workshop er det noen ting som bør være installert og satt opp:

1. Installer Scala-plugin i sitt IDE
       * Eclipse:
              http://scala-ide.org/download/current.html  
              Update sites er litt nede på siden. Velg site for Scala 2.10, og for den versjonen av Eclipse du bruker.  
              Alternativ: Last ned bundle-versjon med Eclipse og Scala plugin: http://scala-ide.org/download/sdk.html
       * IDEA IntelliJ:
              http://confluence.jetbrains.com/display/SCA/Scala+Plugin+for+IntelliJ+IDEA
2. Installer git (http://git-scm.com/)
3. Hent ned prosjektet : `git clone https://github.com/arild/akka-workshop.git`
4. Eksekver `sbt` eller `sbt.bat` fra mappen 'scala-workshop', og skriv enten `eclipse` eller `gen-idea` (avhengig av IDE)
5. Åpne, ikke importer, prosjektet i Eclipse eller IntelliJ
6. Se at tester kjører: eksekver `sbt test` eller `sbt.bat test` fra mappen 'scala-workshop', eventuelt kjør tester via IDE.

Ved problemer med oppsettet, send en mail til arild.nilsen (at) bekk.no eller sjur.millidahl (at) bekk.no
