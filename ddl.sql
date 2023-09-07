drop table if exists users cascade;
drop table if exists items cascade;
drop table if exists bids cascade;
drop table if exists billings cascade;

create table IF NOT EXISTS users (
    user_id VARCHAR(30) PRIMARY KEY,
    password VARCHAR(30),
    is_admin boolean
);

create table IF NOT EXISTS items (
    item_id SERIAL PRIMARY KEY,
    seller_id VARCHAR(30),
    category VARCHAR(30),
    description VARCHAR(100),
    condition VARCHAR(30),
    bin_price integer,
    posted_date timestamp,
    closing_date timestamp,
    status VARCHAR(30),
    FOREIGN KEY(seller_id) references users(user_id)
);

create table IF NOT EXISTS bids (
    item_id integer, 
    bidder_id VARCHAR(30),
    sug_price integer,
    is_highest boolean,
    bidding_date timestamp,
    PRIMARY KEY(item_id, sug_price),
    FOREIGN KEY(item_id) references items(item_id),
    FOREIGN KEY(bidder_id) references users(user_id)
);

create table IF NOT EXISTS billings (
    item_id integer,
    buyer_id VARCHAR(30),
    payment integer,
    sold_date timestamp,
    PRIMARY KEY(item_id),
    FOREIGN KEY(item_id) references items(item_id),
    FOREIGN KEY(buyer_id) references users(user_id)
);