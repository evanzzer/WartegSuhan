# Welcome to Warteg Suhan Server

Beberapa referensi yang berguna untuk menjalankan server ini.

### Cara Memasukan file `suhan.sql` ke database
1. Masukkan semua query dari create table, function dan trigger di dalam database postgresql
2. Masukkan data awal yang telah diberikan di file `suhan.sql`

### Cara mengaktifkan server
1. Buka Command Prompt
2. Masuk ke directory yang memiliki file `index.js`
3. Masukan command `node index.js`
4. Tunggu sampai server terhubung

# Perhatian!
- Pastikan server sudah terhubung dengan database yang benar beserta dengan tabel-tabelnya! (Modifikasi pada `Row 9-16`
- Apabila Port `3000` terpakai oleh aplikasi lain, dapat menggunakan port lain yang tersedia. (Memodifikasi `Line 8`)
