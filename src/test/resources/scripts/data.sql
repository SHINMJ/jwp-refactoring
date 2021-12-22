INSERT INTO menu_group (id, name) VALUES (1, '두마리메뉴');
INSERT INTO menu_group (id, name) VALUES (2, '한마리메뉴');
INSERT INTO menu_group (id, name) VALUES (3, '순살파닭두마리메뉴');
INSERT INTO menu_group (id, name) VALUES (4, '신메뉴');

INSERT INTO product (id, name, price) VALUES (1, '후라이드', 16000);
INSERT INTO product (id, name, price) VALUES (2, '양념치킨', 16000);
INSERT INTO product (id, name, price) VALUES (3, '반반치킨', 16000);
INSERT INTO product (id, name, price) VALUES (4, '통구이', 16000);
INSERT INTO product (id, name, price) VALUES (5, '간장치킨', 17000);
INSERT INTO product (id, name, price) VALUES (6, '순살치킨', 17000);

INSERT INTO menu (id, name, price, menu_group_id) VALUES (1, '후라이드치킨', 16000, 2);

INSERT INTO order_table (id, number_of_guests, table_status) VALUES (1, 0, 'EMPTY');
INSERT INTO order_table (id, number_of_guests, table_status) VALUES (2, 0, 'EMPTY');
INSERT INTO order_table (id, number_of_guests, table_status) VALUES (3, 0, 'EMPTY');
INSERT INTO order_table (id, number_of_guests, table_status) VALUES (4, 0, 'EMPTY');
INSERT INTO order_table (id, number_of_guests, table_status) VALUES (5, 0, 'EMPTY');
INSERT INTO order_table (id, number_of_guests, table_status) VALUES (6, 0, 'EMPTY');
INSERT INTO order_table (id, number_of_guests, table_status) VALUES (7, 0, 'EMPTY');
INSERT INTO order_table (id, number_of_guests, table_status) VALUES (8, 0, 'EMPTY');