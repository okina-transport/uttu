CREATE TABLE IF NOT EXISTS job_import_report (
    job_id bigint PRIMARY KEY REFERENCES job (id),
    error_message character varying(2000)
);

CREATE TABLE IF NOT EXISTS job_import_report_stat (
    report_id bigint NOT NULL REFERENCES job_import_report (job_id),
    stat_key character varying(100) NOT NULL,
    stat_count integer,
    PRIMARY KEY (report_id, stat_key)
);

CREATE TABLE IF NOT EXISTS job_import_report_line (
    report_id bigint NOT NULL REFERENCES job_import_report (job_id),
    list_order integer NOT NULL,
    line_name character varying(255),
    PRIMARY KEY (report_id, list_order)
);

CREATE TABLE IF NOT EXISTS job_import_report_file (
    report_id bigint NOT NULL REFERENCES job_import_report (job_id),
    list_order integer NOT NULL,
    file_name character varying(255),
    valid_file boolean NOT NULL DEFAULT true,
    PRIMARY KEY (report_id, list_order)
);
