# Docker-compose-oppsett for DittNAV med ende-til-ende-tester
Docker-compose-oppsett som brukes for ende-til-ende-tester og kan brukes for å starte alle DittNAV sine avhengigheter lokalt.
Ende-til-ende-testene tester at avhengighetene starter og svarer, samt de vanligste best-case scenarioene for opprettelse av
brukernotifikasjoner og done-eventer.

OBS per nå må vi autentisere oss mot githubs package registry for å hente stub-oidc-provider, dette gjør man ved:
`docker login ghcr.io -u USERNAME -p TOKEN`

Brukernavn og token tilhører githubkontoen din (der tokenet må ha tilgang til read fra package registry)

## Ressursbruk i Docker
NB! Docker må ha minimum 3GB minne tilgjengelig for å kunne håndtere docker-compose oppsettet og kjøre ende-til-ende-testene. 

Dette kan endres i Docker: 
Preferences -> Resources -> Advanced -> Memory: 3.00 GB

## Kjøre ende-til-ende-tester

Ende-til-ende-testene kjøres med `gradle clean test`

Testene kan også kjøres på Github Actions, ved hjelp av repository_dispatch. D.v.s. et POST-kall med gyldig Github-token til 
`https://api.github.com/repos/navikt/dittnav-docker-compose/dispatches`, med payload `{"event_type":"run-tests"}`.

## Legge til ny applikasjon i ende-til-ende-testene

For at ny applikasjon skal kunne være med i ende-til-ende-test-oppsettet må følgende være på plass:
1. Applikasjonen må legges inn som en service i `docker-compose.yml`
2. Legge inn config for den nye applikasjonen, bl.a. portnummer, i `ServiceConfiguration.kt`

## Feilsøke ende-til-ende-testene

Dersom en test feiler kan årsaken ofte finnes i loggene til containeren til avhengigheten der feilen oppstod. Disse kan man få tak
i ved å bruke funksjonen `getLogsFor(<service>)` i `DittNavDockerComposeContainer`, som man har tilgjengelig ved å extende `UsesTheCommonDockerComposeContext`. Denne
klassen må uansett alle ende-til-ende-tester arve fra for å få tilgang til den felles docker-compose-konteksten.

# Kom i gang
1. Stop alle service-er fra dette prosjektet som har blitt startet manuelt: `docker-compose down`
2. Kjør ende-til-ende-testene: `gradle clean build`

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot https://github.com/orgs/navikt/teams/dittnav

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #team-personbruker.
