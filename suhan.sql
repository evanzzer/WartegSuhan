-- PEMBUATAN TABLE
create type jenis_menu as enum ('Makanan', 'Minuman', 'Paket');
create table menu (id_menu serial primary key, jenis_menu jenis_menu, nama_menu varchar(255), jumlah_stok int, harga bigint);
-- transaksi
create table penjualan (id_order serial not null, id_pembeli int not null, id_menu int not null, jumlah_stok int, constraint fk_menu foreign key(id_menu) references menu(id_menu) on update cascade);
-- pembeli
create table keuangan_customer (id_pembeli serial primary key, saldo bigint, pengeluaran bigint);

CREATE OR REPLACE FUNCTION check_stock() RETURNS TRIGGER AS
    $$
    BEGIN
        DECLARE
            sisa_stok int;
        BEGIN
            EXECUTE FORMAT('SELECT jumlah_stok FROM menu WHERE id_menu = $1') into sisa_stok USING new.id_menu;

            IF (sisa_stok < NEW.jumlah_stok) THEN
                RAISE EXCEPTION 'Insufficient Stock for ID Menu %', new.id_menu;
            end if;
            return new;
        end;
    end;
    $$
language plpgsql volatile;

create trigger trigger_check_stock
    before insert on penjualan
    for each row execute procedure check_stock();

-- PEMBUATAN FUNCTION UNTUK MELAKUKAN PENGURANGAN JUMLAH STOK
CREATE OR REPLACE FUNCTION update_stock()
    RETURNS TRIGGER AS
$$
BEGIN
    EXECUTE format ('UPDATE menu SET jumlah_stok = jumlah_stok - $1 WHERE id_menu = $2') USING new.jumlah_stok, new.id_menu;
    return new;
END
$$
    LANGUAGE plpgsql VOLATILE;


--PEMBUATAN TRIGGER FUNCTION TRANSAKSI
CREATE TRIGGER trigger_update_stok
AFTER INSERT ON penjualan
FOR EACH ROW EXECUTE PROCEDURE update_stock();

--PEMBUATAN FUNCTION UNTUK PENGURANGAN PEMBELIAN MENU TERHADAP UANG YANG DIMILIKI USER DAN MENAMBAHKAN TOTAL HARGA YANG DIBELI USER
CREATE OR REPLACE FUNCTION transaksi_customer()
    RETURNS TRIGGER AS
$$
BEGIN
    DECLARE
        id_pembeli int;
        harga bigint;
    BEGIN
        EXECUTE FORMAT('SELECT id_pembeli from keuangan_customer WHERE id_pembeli = $1')
        INTO id_pembeli USING new.id_pembeli;

        EXECUTE FORMAT('SELECT harga FROM menu WHERE id_menu = $1')
        INTO harga USING new.id_menu;

        -- TENTUKAN KALAU HASILNYA SAMA DENGAN ID PEMBELI
        if (id_pembeli IS NULL) then
            EXECUTE FORMAT('INSERT INTO keuangan_customer VALUES ($1, (0 - $2), $2)') USING new.id_pembeli, (new.jumlah_stok * harga);
        ELSE
            EXECUTE FORMAT('UPDATE keuangan_customer SET saldo = (saldo - $1), pengeluaran = (pengeluaran + $1) WHERE id_pembeli = $2') using (new.jumlah_stok * harga), new.id_pembeli;
        end if;
        return new;
    END;
END
$$
    LANGUAGE plpgsql VOLATILE;

--PEMBUATAN TRIGGER FUNCTION transaksi_pembeli
CREATE TRIGGER trigger_update_customer
AFTER INSERT ON penjualan
FOR EACH ROW EXECUTE PROCEDURE transaksi_customer();

--MEMASUKKAN KE TABLE MENU
insert into menu (jenis_menu, nama_menu, jumlah_stok, harga) values
('Makanan','Nasi Goreng', 3, 10000), -- 1
('Makanan','Ayam Goreng', 4, 12000),
('Makanan','Sayur Mayur', 2, 5000),
('Makanan','Tempe', 7, 2500),
('Makanan','Tahu', 8, 2500),
('Makanan','Telor', 7, 2000),
('Minuman','Extra Joss', 12, 3000),
('Minuman','Teh Manis', 15, 1500),
('Minuman','Es Jeruk', 11, 4000),
('Makanan','Indomie Goreng', 9, 5000),
('Makanan','Indomie kuah', 10, 5000), -- 1
('Makanan','Steak ayam', 11, 15000),
('Makanan','Kentang goreng', 7, 10000),
('Paket','Paket sederhana 1', 5, 13000),
('Paket','Paket sederhana 2', 5, 14000);

--MEMASUKKAN DATA KEUANGAN PEMBELI
insert into keuangan_customer values (1, 5000, 0), (2, 10000, 0);
 
-- masukin data jika ada yang ingin beli 
insert into penjualan (id_pembeli, id_menu, harga) values (2, 3, 2);
insert into penjualan (id_pembeli, id_menu, harga) values (3, 1, 1);


-- masukin data untuk topup uang
update keuangan_customer set saldo = saldo + 10000 where id_pembeli = 2;

-- select data 
select * from menu order by id_menu asc;
select * from penjualan order by id_order asc;
select * from keuangan_customer order by id_pembeli asc;



