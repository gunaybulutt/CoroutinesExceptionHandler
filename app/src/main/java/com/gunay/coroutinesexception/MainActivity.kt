package com.gunay.coroutinesexception

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //CoroutineExceptionHandler -->coroutineler için olan try-catch benzeri yapı
        //coroutine'ler için hata yakalama amacıyla kullanılması gerek yapıdır
        //coroutineContext --> coroutine hakkında bilgilere ulaşmak için kullanılabilir
        //throwable --> hatanın almak kullanılabilir (print ile)
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("exception: " + throwable.localizedMessage)
        }


        //activity içinde coroutine oluşturmak için kullanılır(yaşam döngüsü sahibi içinde çalıştırılır)
        //CoroutineExceptionHandler bu şekilde coroutine'lere eklenebilir (handler) -- handler context olarak verilir
        lifecycleScope.launch(handler) {
            //kodun hata vermesini sağlar
            // kod hata vericek fakat CoroutineExceptionHandler'ı lifecycleScope'a eklediğimiz için sanki try-catch içindeymiş gibi olucak
            // ve kod çökmeden devam edicek aynı zamanda handler hatayı print ederek log'da verecektir
            throw Exception("eror")
        }

        lifecycleScope.launch(handler) {
            //iç içe launch yapılarındada fark etmeksizin handler çalışacaktır
            launch {
                throw Exception("eror2")

            }
        }


        // bu tarz birçok launch'un birlikte kullanıldığı yapılarda hata alındıktan sonra bütün scpoe çalışmayı durduracaktır
        // bu nedenle eror before çıktısı verilecek ama eror after çoktısına gelmeden hata alınacağı için scope çalışmayı durduracak ve eror after çıktısı alınmayacaktır
        lifecycleScope.launch(handler) {

            launch {
                println("eror before")
            }

            launch {
                throw Exception("eror3")

            }

            launch {
                println("eror after")
            }
        }

        // çoklu lauch kullanıldığında hata alınsa bile lauch'ların birbirinden bağımsız(hatalarda vs) çalışarak scope'un çalışmayı bırakması engellenmek isteniyorsa -->
        // o zaman supervisorScope kullanılır
        lifecycleScope.launch(handler) {

            //supervisorScope --> bu scope sayesinde launchlar birbirinden bağımsız çalışır bir launch'tan hata alınsa bile diğerleri çalıştırılmaya devam eder
            //supervisorScope gözlemci bir scope'dur
            supervisorScope {
                launch {
                    println("eror before super")
                }

                launch {
                    throw Exception("eror super ")

                }

                launch {
                    println("eror after super")
                }
            }
        }

        //handler bu şekildede kullanılabilir
        CoroutineScope(Dispatchers.Main + handler).launch{
            throw Exception("CoroutineScope eror")
        }

    }
}