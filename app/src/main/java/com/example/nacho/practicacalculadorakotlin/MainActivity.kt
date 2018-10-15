package com.example.nacho.practicacalculadorakotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.support.design.widget.Snackbar;
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var listaOperacion : MutableList<String> = mutableListOf()
    var listaNumeros : String = ""
    var igualPulsado = false
    var memoria : Float = 0.0F
    var binarioActivo = false
    var decimalActivo = true
    var hexaActivo = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState!=null) {
            textView2.text = savedInstanceState!!.getString("pantalla1")
            listaNumeros = savedInstanceState!!.getString("listaN")
            memoria = savedInstanceState!!.getFloat("memoria")
            decimalActivo = savedInstanceState!!.getBoolean("decimal")
            binarioActivo = savedInstanceState!!.getBoolean("binario")
            hexaActivo = savedInstanceState!!.getBoolean("hexa")

            if (binarioActivo || hexaActivo) {

                decimalOnRestore()

            }

            listaOperacion.clear()

        }

    }

    fun pulsarOperacion (v : View) {

        if(igualPulsado==true){
            igualPulsado=igualPulsado.not()
            listaOperacion.clear()

        }

        if(listaNumeros.isNotEmpty()){
            val botonPulsado = findViewById<Button>(v.id)
            val operacionBotonPulsado = botonPulsado.text.toString()
            listaOperacion.add(listaNumeros)
            listaOperacion.add(operacionBotonPulsado)
            calcular(v)
            listaNumeros = ""
        }
    }

    fun pulsarNumero (v : View){

        if (igualPulsado == true) {
            igualPulsado = false
            clear(v)
        }

        val botonPulsado = findViewById<Button>(v.id)
        val numeroBotonPulsado = botonPulsado.text.toString()

        if (listaNumeros.contains(".") && numeroBotonPulsado == ".") {

        } else {
            if (listaNumeros.length < 12) {
                listaNumeros = "$listaNumeros$numeroBotonPulsado"
                actualizarPantalla(v, listaNumeros)
            } else {
                Snackbar.make(v, "El número tecleado es demasiado grande", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun actualizarPantalla (v : View, listaN : String ) {

        if (listaN.length < 13) {

            val displayGrande = textView2 as TextView
            displayGrande.text = listaN

            val displayPeque = textView as TextView
            var textoDisplayPeque = ""
            for (operacion in listaOperacion) {
                textoDisplayPeque = "$textoDisplayPeque$operacion"
            }
            displayPeque.text = textoDisplayPeque

        }else{
            Snackbar.make(v, "El número es demasiado grande",Snackbar.LENGTH_LONG).show()
        }


    }

    fun limpiarTodo(v : View){

        clear(v)

    }

    fun igual(v : View){

        if(listaNumeros.isNotEmpty() && listaOperacion.isNotEmpty() && igualPulsado==false) {
            listaOperacion.add(listaNumeros)

            calcular(v)

            igualPulsado = true

            val displayGrande = textView2 as TextView
            listaNumeros = displayGrande.text.toString()
        }
    }

    fun calcular( v: View) {

        var operacionActual = ""
        var total = 0.0F

        for (operacion in listaOperacion) {

            if (operacion == "+" || operacion == "-" || operacion == "*" || operacion == "/")
                operacionActual = operacion
            else

                if (binarioActivo) {
                    when {
                        operacionActual == "" -> total = toDecimal(operacion).toFloat()
                        operacionActual == "+" -> total += toDecimal(operacion).toFloat()
                        operacionActual == "-" -> total -= toDecimal(operacion).toFloat()
                        operacionActual == "*" -> total *= toDecimal(operacion).toFloat()
                        operacionActual == "/" -> total /= toDecimal(operacion).toFloat()
                    }

                } else if (decimalActivo) {

                    when {
                        operacionActual == "" -> total = operacion.toFloat()
                        operacionActual == "+" -> total += operacion.toFloat()
                        operacionActual == "-" -> total -= operacion.toFloat()
                        operacionActual == "*" -> total *= operacion.toFloat()
                        operacionActual == "/" -> total /= operacion.toFloat()
                    }

                }else{

                    when {
                        operacionActual == "" -> total = java.lang.Long.parseLong(operacion, 16).toFloat()
                        operacionActual == "+" -> total += java.lang.Long.parseLong(operacion, 16).toFloat()
                        operacionActual == "-" -> total -= java.lang.Long.parseLong(operacion, 16).toFloat()
                        operacionActual == "*" -> total *= java.lang.Long.parseLong(operacion, 16).toFloat()
                        operacionActual == "/" -> total /= java.lang.Long.parseLong(operacion, 16).toFloat()
                    }

                }

        }

        if (binarioActivo) {
            actualizarPantalla(v, toBinary(total.toLong()))
        } else if (decimalActivo) {
            val totalSinDecimales: Long = total.toLong()
            if (totalSinDecimales.toFloat() == total) {
                actualizarPantalla(v, totalSinDecimales.toString())
            } else {
                actualizarPantalla(v, total.toString())
            }
        } else {
            actualizarPantalla(v, conversionDecHex(total.toLong()))
        }

    }

    fun clear(v : View){

        listaOperacion.clear()
        listaNumeros=""
        actualizarPantalla(v,listaNumeros)
        igualPulsado = false

    }

    fun memoria(v :View){

        val identificador = findViewById<Button>(v.id)
        val idenString = identificador.text.toString()

        val displayGrande = textView2 as TextView

        when(idenString){
            "MS" -> memoria = displayGrande.text.toString().toFloat()
            "M+" -> memoria += displayGrande.text.toString().toFloat()
            "M-" -> memoria -= displayGrande.text.toString().toFloat()
            "MC" -> memoria = 0.0F
            "MR" -> {
                listaNumeros=memoria.toString()
                actualizarPantalla(v,listaNumeros)
            }

        }

    }

    fun borrar(v : View){
        if(listaNumeros.length>0)
        listaNumeros=listaNumeros.substring(0,listaNumeros.length-1)
        textView2.text=listaNumeros
    }

    fun negar(v:View){
        if(textView2.text.startsWith("-")){
            listaNumeros = listaNumeros.substring(1,textView2.length())
            textView2.text = listaNumeros
        }else{
            listaNumeros = "-$listaNumeros"
            textView2.text = listaNumeros
        }

    }

    fun decimal(v : View){

        button2.visibility=View.VISIBLE
        button3.visibility=View.VISIBLE
        button4.visibility=View.VISIBLE
        button5.visibility=View.VISIBLE
        button6.visibility=View.VISIBLE
        button7.visibility=View.VISIBLE
        button8.visibility=View.VISIBLE
        button9.visibility=View.VISIBLE
        buttonA.visibility=View.INVISIBLE
        buttonB.visibility=View.INVISIBLE
        buttonC.visibility=View.INVISIBLE
        buttonD.visibility=View.INVISIBLE
        buttonE.visibility=View.INVISIBLE
        buttonF.visibility=View.INVISIBLE

        if(binarioActivo){

            var conversion = 0L;
            if(textView2.text!=""){
                conversion=toDecimal(textView2.text.toString())
            }


            limpiar()

            listaNumeros=conversion.toString()
            textView2.text=conversion.toString()

        }else if(hexaActivo){

            var conversion = 0L;
            if(textView2.text!=""){
                conversion=java.lang.Long.parseLong(textView2.text.toString(), 16)
            }

            limpiar()

            listaNumeros=conversion.toString()
            textView2.text=conversion.toString()

        }

        binarioActivo = false
        decimalActivo = true
        hexaActivo = false

    }

    fun binario (v : View){

        button2.visibility=View.INVISIBLE
        button3.visibility=View.INVISIBLE
        button4.visibility=View.INVISIBLE
        button5.visibility=View.INVISIBLE
        button6.visibility=View.INVISIBLE
        button7.visibility=View.INVISIBLE
        button8.visibility=View.INVISIBLE
        button9.visibility=View.INVISIBLE
        buttonA.visibility=View.INVISIBLE
        buttonB.visibility=View.INVISIBLE
        buttonC.visibility=View.INVISIBLE
        buttonD.visibility=View.INVISIBLE
        buttonE.visibility=View.INVISIBLE
        buttonF.visibility=View.INVISIBLE


        if(decimalActivo){

            var preConversion = 0;
            if(textView2.text!=""){
                preConversion = Math.round(textView2.text.toString().toFloat())
            }


            val conversion=toBinary(preConversion.toLong())

            clear(v)

            listaNumeros=conversion
            textView2.text=conversion

        }else if (hexaActivo){

            var conversion = 0L;
            if(textView2.text!=""){
                conversion=java.lang.Long.parseLong(textView2.text.toString(), 16)
            }

            val segundaConversion=toBinary(conversion)

            clear(v)

            listaNumeros=segundaConversion
            textView2.text=segundaConversion

        }

        binarioActivo = true
        decimalActivo = false
        hexaActivo = false

    }

    fun hexadecimal(v : View){

        button2.visibility=View.VISIBLE
        button3.visibility=View.VISIBLE
        button4.visibility=View.VISIBLE
        button5.visibility=View.VISIBLE
        button6.visibility=View.VISIBLE
        button7.visibility=View.VISIBLE
        button8.visibility=View.VISIBLE
        button9.visibility=View.VISIBLE
        buttonA.visibility=View.VISIBLE
        buttonB.visibility=View.VISIBLE
        buttonC.visibility=View.VISIBLE
        buttonD.visibility=View.VISIBLE
        buttonE.visibility=View.VISIBLE
        buttonF.visibility=View.VISIBLE

        if (decimalActivo) {

            var preConversion = 0;
            if(textView2.text!=""){
                preConversion = Math.round(textView2.text.toString().toFloat())
            }

            val conversion = conversionDecHex(preConversion.toLong())

            clear(v)

            listaNumeros=conversion
            textView2.text=conversion

        }else if(binarioActivo){

            var conversion = 0L;
            if(textView2.text!=""){
                conversion=toDecimal(textView2.text.toString())
            }

            val segundaConversion = conversionDecHex(conversion.toLong())

            clear(v)

            listaNumeros=segundaConversion
            textView2.text=segundaConversion

        }

        binarioActivo = false
        decimalActivo = false
        hexaActivo = true

    }

    fun toBinary(decimalNumber: Long, binaryString: String = "") : String {
        while (decimalNumber > 0) {
            val temp = "${binaryString}${decimalNumber%2}"
            return toBinary(decimalNumber/2, temp)
        }
        return binaryString.reversed()
    }

    fun toDecimal(binaryNumber : String) : Long {
        var sum = 0.0
        binaryNumber.reversed().forEachIndexed {
            k, v -> sum += v.toString().toInt() * Math.pow(2.0, k.toDouble())
        }
        val sumInt = sum.toLong()
        return sumInt
    }

    fun conversionDecHex(conversion : Long) : String{

        var conversionSegunda = conversion
        val digits = "0123456789ABCDEF"
        if (conversionSegunda <= 0) return "0"
        val base = 16
        var hex = ""
        while (conversionSegunda > 0) {
            val digit = conversionSegunda % base
            hex = digits[digit.toInt()] + hex
            conversionSegunda = conversionSegunda / base
        }
        return hex

    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState!!.putString("pantalla1",textView2.text.toString())
        savedInstanceState!!.putFloat("memoria",memoria)
        savedInstanceState!!.putString("listaN",listaNumeros)
        savedInstanceState!!.putBoolean("decimal",decimalActivo)
        savedInstanceState!!.putBoolean("binario",binarioActivo)
        savedInstanceState!!.putBoolean("hexa",hexaActivo)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        textView2.text = savedInstanceState!!.getString("pantalla1")
        listaNumeros = savedInstanceState!!.getString("listaN")
        memoria = savedInstanceState!!.getFloat("memoria")
        decimalActivo = savedInstanceState!!.getBoolean("decimal")
        binarioActivo = savedInstanceState!!.getBoolean("binario")
        hexaActivo = savedInstanceState!!.getBoolean("hexa")

        if (binarioActivo || hexaActivo) {

            decimalOnRestore()

        }

        listaOperacion.clear()

        super.onRestoreInstanceState(savedInstanceState)
    }

    fun decimalOnRestore (){

        button2.visibility=View.VISIBLE
        button3.visibility=View.VISIBLE
        button4.visibility=View.VISIBLE
        button5.visibility=View.VISIBLE
        button6.visibility=View.VISIBLE
        button7.visibility=View.VISIBLE
        button8.visibility=View.VISIBLE
        button9.visibility=View.VISIBLE

        if(binarioActivo){

            var conversion = 0L;
            if(textView2.text!=""){
                conversion=toDecimal(textView2.text.toString())
            }


            limpiar()

            listaNumeros=conversion.toString()
            textView2.text=conversion.toString()

        }else if(hexaActivo){

            var conversion = 0L;
            if(textView2.text!=""){
                conversion=java.lang.Long.parseLong(textView2.text.toString(), 16)
            }

            limpiar()

            listaNumeros=conversion.toString()
            textView2.text=conversion.toString()

        }

        binarioActivo = false
        decimalActivo = true
        hexaActivo = false

    }

    fun limpiar(){

        listaOperacion.clear()
        listaNumeros=""
        if (listaNumeros.length < 13) {

            val displayGrande = textView2 as TextView
            displayGrande.text = listaNumeros

            val displayPeque = textView as TextView
            var textoDisplayPeque = ""
            for (operacion in listaOperacion) {
                textoDisplayPeque = "$textoDisplayPeque$operacion"
            }
            displayPeque.text = textoDisplayPeque

        }else{

        }
        igualPulsado = false

    }

}
