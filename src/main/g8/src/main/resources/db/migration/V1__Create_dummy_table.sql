CREATE TABLE "dummy" (
  "id"       BIGSERIAL PRIMARY KEY ,
  "dummy" VARCHAR NOT NULL
);

INSERT INTO "dummy" ("dummy") VALUES ('a'), ('b'), ('c'), ('d');