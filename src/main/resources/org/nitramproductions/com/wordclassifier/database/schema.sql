CREATE TABLE IF NOT EXISTS "GROUP" (
    NAME VARCHAR_IGNORECASE(511) NOT NULL PRIMARY KEY,
    DATE_MODIFIED TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP(),
    CHECK LENGTH(TRIM(NAME)) > 0
);

CREATE TABLE IF NOT EXISTS "EXPRESSION" (
    CONTENT VARCHAR_IGNORECASE(511) NOT NULL PRIMARY KEY,
    DATE_MODIFIED TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP(),
    CHECK LENGTH(TRIM(CONTENT)) > 0
);

CREATE TABLE IF NOT EXISTS "BELONGS_TO" (
    NAME VARCHAR_IGNORECASE(511) NOT NULL,
    CONTENT VARCHAR_IGNORECASE(511) NOT NULL,
    PRIMARY KEY (NAME, CONTENT),
    FOREIGN KEY (NAME) REFERENCES "GROUP"(NAME)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (CONTENT) REFERENCES "EXPRESSION"(CONTENT)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);