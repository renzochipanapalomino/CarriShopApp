BEGIN TRANSACTION;

-- ===== Tablas =====
CREATE TABLE IF NOT EXISTS supermercados(
  id INTEGER PRIMARY KEY,
  nombre TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS productos(
  id INTEGER PRIMARY KEY,
  nombre TEXT NOT NULL,
  nombre_normalizado TEXT NOT NULL UNIQUE,
  marca TEXT,
  categoria TEXT
);

CREATE TABLE IF NOT EXISTS precios(
  id INTEGER PRIMARY KEY,
  producto_id INTEGER NOT NULL,
  supermercado_id INTEGER NOT NULL,
  precio REAL NOT NULL,
  UNIQUE(producto_id, supermercado_id) ON CONFLICT REPLACE
);

-- ===== Supermercados =====
INSERT OR IGNORE INTO supermercados (id, nombre) VALUES
  (1,'Tottus'),
  (2,'Plaza Vea'),
  (3,'Saga Falabella'),
  (4,'Ripley'),
  (5,'Mass');

-- ===== Productos (30 registros) =====
INSERT OR IGNORE INTO productos (id,nombre,nombre_normalizado,marca,categoria) VALUES
  (1,'Leche Gloria 1L','leche gloria','Gloria','Lácteos'),
  (2,'Leche evaporada light 400 ml','leche evaporada light','Gloria','Lácteos'),
  (3,'Yogurt bebible 500 ml','yogurt bebible','Laive','Lácteos'),
  (4,'Queso fresco 500 g','queso fresco','Gloria','Lácteos'),
  (5,'Fideos spaghetti 500 g','fideos spaghetti','Don Vittorio','Pastas'),
  (6,'Fideos tornillo 500 g','fideos tornillo','Don Vittorio','Pastas'),
  (7,'Arroz extra 5 kg','arroz extra 5kg','Costeño','Granos'),
  (8,'Lentejas 1 kg','lentejas','Costeño','Granos'),
  (9,'Aceite vegetal 1L','aceite vegetal','Primor','Aceites'),
  (10,'Aceite de oliva 500 ml','aceite de oliva','Carbonell','Aceites'),
  (11,'Azúcar blanca 1 kg','azucar blanca','Cartavio','Endulzantes'),
  (12,'Azúcar rubia 1 kg','azucar rubia','Cartavio','Endulzantes'),
  (13,'Sal yodada 1 kg','sal yodada','Cajamarquilla','Condimentos'),
  (14,'Atún en agua 170 g','atun en agua','Florida','Conservas'),
  (15,'Sardinas en salsa 170 g','sardinas salsa','Florida','Conservas'),
  (16,'Pan de molde 600 g','pan de molde','Bimbo','Panificados'),
  (17,'Pan francés (10 und)','pan frances','Bimbo','Panificados'),
  (18,'Café instantáneo 100 g','cafe instantaneo','Nescafé','Bebidas calientes'),
  (19,'Café pasado 250 g','cafe pasado','Altomayo','Bebidas calientes'),
  (20,'Té en bolsitas 20 und','te bolsitas','Hornimans','Bebidas calientes'),
  (21,'Gaseosa Inca Kola 1.5 L','gaseosa inca kola','Inca Kola','Bebidas'),
  (22,'Gaseosa Coca-Cola 1.5 L','gaseosa coca cola','Coca-Cola','Bebidas'),
  (23,'Jugo durazno 1L','jugo durazno','Watts','Bebidas'),
  (24,'Agua mineral 625 ml','agua mineral','San Mateo','Bebidas'),
  (25,'Detergente en polvo 1 kg','detergente polvo','Ace','Limpieza'),
  (26,'Lavavajilla líquido 750 ml','lavavajilla','Sapolio','Limpieza'),
  (27,'Papel higiénico 4 rollos','papel higienico','Elite','Higiene'),
  (28,'Shampoo 400 ml','shampoo 400ml','Sedal','Cuidado personal'),
  (29,'Desodorante aerosol 150 ml','desodorante aerosol','Rexona','Cuidado personal'),
  (30,'Crema dental 90 g','crema dental','Colgate','Cuidado personal');

-- ===== Precios base (TOTTUS) =====
INSERT OR REPLACE INTO precios (producto_id,supermercado_id,precio) VALUES
  (1,1,4.90),(2,1,5.10),(3,1,5.80),(4,1,13.90),
  (5,1,4.80),(6,1,4.70),(7,1,22.50),(8,1,6.00),
  (9,1,10.90),(10,1,22.90),(11,1,4.50),(12,1,4.30),
  (13,1,2.30),(14,1,6.90),(15,1,7.20),(16,1,6.00),
  (17,1,4.50),(18,1,18.50),(19,1,22.90),(20,1,9.50),
  (21,1,8.00),(22,1,8.20),(23,1,6.00),(24,1,2.00),
  (25,1,12.50),(26,1,7.30),(27,1,6.50),(28,1,15.50),
  (29,1,14.40),(30,1,6.10);

-- PLAZA VEA (+3%)
INSERT OR REPLACE INTO precios (producto_id,supermercado_id,precio)
SELECT producto_id,2,ROUND(precio*1.03,2) FROM precios WHERE supermercado_id=1;

-- SAGA FALABELLA (+7%)
INSERT OR REPLACE INTO precios (producto_id,supermercado_id,precio)
SELECT producto_id,3,ROUND(precio*1.07,2) FROM precios WHERE supermercado_id=1;

-- RIPLEY (+6%)
INSERT OR REPLACE INTO precios (producto_id,supermercado_id,precio)
SELECT producto_id,4,ROUND(precio*1.06,2) FROM precios WHERE supermercado_id=1;

-- MASS (-8%)
INSERT OR REPLACE INTO precios (producto_id,supermercado_id,precio)
SELECT producto_id,5,ROUND(precio*0.92,2) FROM precios WHERE supermercado_id=1;

COMMIT;
