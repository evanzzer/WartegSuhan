# Welcome to Warteg Suhan

Warteg SUHAN merupakan warteg yang menjual berbagai macam makanan dan minuman. 
Warteg SUHAN membuat sebuah database untuk menyimpan berbagai macam transaksi yang dilakukan oleh pembeli atau customer. 
Pembeli dapat melakukan topup sehingga saldo bertambah atau membeli makanan sehingga saldo berkurang. 
Terdapat pula stok setiap item yang dapat berkurang atau bertambah.


## Warteg Suhan Server
Beberapa referensi yang berguna untuk menjalankan server ini.

### Cara Memasukan file `suhan.sql` ke database
1. Masukkan semua query dari create table, function dan trigger di dalam database postgresql
2. Masukkan data awal yang telah diberikan di file `suhan.sql`

### Cara mengaktifkan server
1. Buka Command Prompt
2. Masuk ke directory yang memiliki file `index.js`
3. Masukan command `node index.js`
4. Tunggu sampai server terhubung

## Perhatian!
- Pastikan server sudah terhubung dengan database yang benar beserta dengan tabel-tabelnya! (Modifikasi pada `Row 9-16`
- Apabila Port `3000` terpakai oleh aplikasi lain, dapat menggunakan port lain yang tersedia. (Memodifikasi `Line 8`)

## Warteg Suhan Android App

Terdapat beberapa fitur yang tersedia untuk aplikasi yang khusus untuk **Warteg Suhan**
1. Melihat, menambahkan, mengedit, dan menghapus list menu beserta stoknya.
2. Topup saldo yang tersedia untuk setiap customer baru dan yang sudah ada. 
   Terdapat fitur ID Customer yang bersifat anonim dan dilengkapi dengan kartu yang mengidentifikasi customer tersebut. 
   Terdapat pula jumlah uang yang telah dihabiskan oleh customer tertentu.
3. Kasir. Tempat untuk melakukan ordering. Semua transaksi dilakukan secara kredit (Pembayaran dilakukan dalam TopUp)
4. List Customer beserta saldonya dan jumlah uang yang telah dikeluarkan oleh customer tersebut.
5. Riwayat Transaksi. Melihat transaksi-transaksi yang telah dilakukan oleh customer.
6. Aplikasi yang dapat menyesuaikan dengan tema Android (Light/Dark Mode)

### Cara menghubungkan Aplikasi dengan Server.
Andaikata server dijalankan dalam komputer pribadi alias `localhost`, maka beberapa metode untuk menghubungkannya adalah sebagai berikut.
1. Jika dijalankan dalam emulator, pada menu utama yang terdapat kolom URL, diisi dengan `10.0.2.2:3000` (Port pada Server adalah 3000. 
   Sesuaikan jika server memiliki port yang berbeda)
2. Jika dilakukan dalam Smartphone Pribadi, pada menu utama yang terdapat kolom URL, diisi dengan IP Address dari komputer yang menjalankan server. 
   Contoh: `192.168.1.110:3000`

## Warning!
### Aplikasi ini tidak mendukung iOS


