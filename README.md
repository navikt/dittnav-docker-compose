# Docker-compose-oppsett for DittNAV med ende-til-ende-tester
Docker-compose-oppsett som brukes for ende-til-ende-tester og kan brukes for å starte alle DittNAV sine avhengigheter lokalt.

OBS per nå må vi autentisere oss mot githubs package registry for å hente stub-oidc-provider, dette gjør man ved:
`docker login docker.pkg.github.com -u USERNAME -p TOKEN`

Brukernavn og token tilhører githubkontoen din (der tokenet må ha tilgang til read fra package registry)

# Kom i gang
1. Stop alle service-er fra dette prosjektet som har blitt startet manuelt: `docker-compose down`
2. Kjør ende-til-ende-testene: `gradle clean build`

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes mot https://github.com/orgs/navikt/teams/dittnav

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #team-personbruker.
