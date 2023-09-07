

INSERT INTO USERS values('admin',	'0000',	true);
INSERT INTO USERS values('user1',	'0000',	false);
INSERT INTO USERS values('user2',	'0000',	false);
INSERT INTO USERS values('user3',	'0000',	false);
INSERT INTO USERS values('user4',	'0000',	false);
INSERT INTO USERS values('user5',	'0000',	false);

INSERT INTO ITEMS values(DEFAULT,	'user1',	'ELECTRONICS',	'Headphone',	    'NEW',	        120000,	'2023-03-08 12:10:00.111657',	'2023-03-10 12:10:00',	'EXPIRED');
INSERT INTO ITEMS values(DEFAULT,	'user1',	'CLOTHING',	    'Training Pants',	'LIKE_NEW',	    7500,	'2023-03-08 12:10:00.111657',	'2023-03-10 12:10:00',	'EXPIRED');
INSERT INTO ITEMS values(DEFAULT,	'user2',	'SPORTINGGOODS','Pullup Bar',	    'GOOD',	        40000,	'2023-04-12 11:43:00.111657',	'2023-06-21 08:30:00',	'AVAILABLE');
INSERT INTO ITEMS values(DEFAULT,	'user3',	'BOOKS',	    'Operating System', 'NEW',	        20000,	'2023-03-13 10:50:00.111657',	'2023-06-19 07:00:00',	'AVAILABLE');
INSERT INTO ITEMS values(DEFAULT,	'user4',	'BOOKS',	    'Database',	        'ACCEPTABLE',	15000,	'2023-02-15 20:00:00.111657',	'2023-05-30 22:50:00',	'SOLD');
INSERT INTO ITEMS values(DEFAULT,	'user5',	'HOME',	        'Chair',	        'GOOD',	        30000,	'2023-03-03 11:05:00.111657',	'2023-05-30 21:30:00',	'SOLD');
INSERT INTO ITEMS values(DEFAULT,	'user1',	'CLOTHING',	    'Purple Jacket',	'NEW',	        24000,	'2023-05-01 01:20:32.111657',	'2023-07-01 10:00:00',	'SOLD');
INSERT INTO ITEMS values(DEFAULT,	'user1',	'HOME',	        'Broomstick',	    'LIKE_NEW',	    1200,	'2023-05-01 01:29:03.223454',	'2023-06-11 19:00:00',	'SOLD');
INSERT INTO ITEMS values(DEFAULT,	'user1',	'HOME',	        'fork',	            'GOOD',	        1000,	'2023-05-01 01:59:08.732824',	'2023-09-11 10:00:00',	'AVAILABLE');
INSERT INTO ITEMS values(DEFAULT,'user1',	'BOOKS',	    'computer system',  'LIKE_NEW',	    11000,	'2023-05-01 02:20:24.084019',	'2023-05-01 02:22:00',	'SOLD');
INSERT INTO ITEMS values(DEFAULT,'user1',	'ELECTRONICS',	'iphone xs',	    'NEW',	        100000,	'2023-05-01 02:22:21.328249',	'2023-05-01 02:24:00',	'SOLD');
INSERT INTO ITEMS values(DEFAULT,'user1',	'CLOTHING',	    'hair band',	    'NEW',	        1015,	'2023-05-01 02:48:03.467121',	'2023-05-10 10:10:00',	'SOLD');

INSERT INTO BIDS  values( 4	,   'user4',  900,   false,	'2023-03-14 10:50:00.936592' );
INSERT INTO BIDS  values( 4	,   'user5',  15000, true,	'2023-03-15 12:30:00.936592' );
INSERT INTO BIDS  values( 5	,   'user1',  17000, true,	'2023-02-17 20:00:00.936592' );
INSERT INTO BIDS  values( 6	,   'user1',  30000, true,	'2023-04-01 00:05:00.936592' );
INSERT INTO BIDS  values( 7	,   'user2',  24000, true,	'2023-05-01 01:24:11.936592' );
INSERT INTO BIDS  values( 8	,   'user2',  1000 , false,	'2023-05-01 02:03:05.944494' );
INSERT INTO BIDS  values( 8	,   'user2',  1100 , false,	'2023-05-01 02:03:22.442654' );
INSERT INTO BIDS  values( 10,   'user2',  12000, true,	'2023-05-01 02:20:52.608038' );
INSERT INTO BIDS  values( 11,   'user2',  90000, true,	'2023-05-01 02:22:47.454032' );
INSERT INTO BIDS  values( 8	,   'user3',  1150 , false,	'2023-05-01 02:04:16.685414' );
INSERT INTO BIDS  values( 8	,   'user3',  1300 , true,	'2023-05-01 02:34:18.224871' );
INSERT INTO BIDS  values( 12,   'user4',  1300 , true,	'2023-05-01 02:48:27.538131' );

INSERT INTO BILLINGS values(5,	'user1',	15000,	'2023-02-17 20:00:00.944882');
INSERT INTO BILLINGS values(6,	'user1',	30000,	'2023-04-01 00:05:00.944882');
INSERT INTO BILLINGS values(7,	'user2',	24000,	'2023-05-01 01:24:11.944882');
INSERT INTO BILLINGS values(10,	'user2',	11000,	'2023-05-01 02:20:52.616601');
INSERT INTO BILLINGS values(11,	'user2',	90000,	'2023-05-01 02:24:03.895021');
INSERT INTO BILLINGS values(8,	'user3',	1200,   '2023-05-01 02:34:18.232591');
INSERT INTO BILLINGS values(12,	'user4',	1015,   '2023-05-01 02:48:27.546071');




