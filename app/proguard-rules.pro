# NOTEZ release rules — R8 full untuk susutkan APK
# Room: simpan entities, DAO, dan generated impl (wajib diverifikasi di device)
-keep class com.zaba.notez.Note { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao class * { *; }
-keep class * implements androidx.room.RoomDatabase$Callback { *; }
# RecyclerView adapter dipanggil via reflection? Tidak — tapi simpan ViewHolder aman
-keep class com.zaba.notez.NoteAdapter$Holder { *; }
# Material/AppCompat: biarkan R8 + consumer rules bawaan bekerja, tanpa keep global
-dontwarn java.lang.invoke.**
