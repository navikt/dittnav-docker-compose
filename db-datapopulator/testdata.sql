CREATE TABLE IF NOT EXISTS systembrukere (
    systembruker character varying(50) not null primary key,
    produsentnavn character varying(100) not null
);

INSERT INTO systembrukere(systembruker, produsentnavn) VALUES ('dittnav', 'dittnav') ON CONFLICT DO NOTHING;
