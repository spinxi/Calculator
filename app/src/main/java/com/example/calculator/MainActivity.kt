package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit private var textView: TextView;
    private var canAddOperation:Boolean = false;
    private var canAddDecimal:Boolean = false;
    private var canDeleteParentheses:Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textView = findViewById(R.id.textView)
    }

    fun allClear(view: View) {
        textView.text = ""
    }
    fun operationAction(view:View){
        if(view is Button && canAddOperation){
            textView.append(view.text)
            canAddOperation = false
        }
    }


    fun numberAction(view: View) {

        if (view is Button) {
            if(view.text == "."){
                if(canAddDecimal){
                    textView.append(view.text)
                    canAddDecimal = false
                    canAddOperation = false
                }
            }else if(textView.text.isNotEmpty() && textView.text.last() == ')'){
                canAddOperation = true
            }else{
                textView.append(view.text)
                canAddDecimal = true
                canAddOperation = true
            }
        }
    }

    fun equalsAction(view: View){
        if (view is Button) {
            textView.text = calculateResults();
        }
    }

    private fun calculateResults(): String {

        val digitsOperators = filterDigitsOrOperators();

        if (digitsOperators.isEmpty()){
            return ""
        }
        val timesDivision = timesDivision(digitsOperators);
        if (timesDivision.isEmpty()){
            return ""
        }

        val result = addSubstractCalculator(timesDivision);
        return result.toString();


    }

    private fun addSubstractCalculator(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float;
        for (i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex){
                val operator = passedList[i];
                val nextDigit = passedList[i + 1] as Float;
                when(operator){
                    '-' ->
                    {
                        result -= nextDigit;
                    }
                    '+' ->
                    {
                        result += nextDigit;
                    }
                }
            }
        }
        return result;
    }

    private fun timesDivision(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList;
        while (list.contains('x') || list.contains('/') || list.contains('%') ){
            list = calculateTimesDivision(list);
        }
        return list;
    }

    private fun calculateTimesDivision(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>();
        var restartIndex = passedList.size;

        for(i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex){
                val operator = passedList[i];
                val prevDigit = passedList[i-1] as Float;
                val nextDigit = passedList[i+1] as Float;

                when(operator){
                    'x' ->
                    {
                        newList.add(prevDigit * nextDigit);
                        restartIndex = i + 1;
                    }
                    '/' ->
                    {
                        newList.add(prevDigit / nextDigit);
                        restartIndex = i + 1;
                    }
                    '%' ->
                    {
                        newList.add((prevDigit * nextDigit) / 100);
                        restartIndex = i + 1;
                    }
                    else ->
                    {
                        newList.add(prevDigit);
                        newList.add(operator);
                    }
                }
            }
            if(i > restartIndex){
                newList.add(passedList[i]);
            }
        }

        return newList;
    }

    private fun filterDigitsOrOperators(): MutableList<Any>{
        val list = mutableListOf<Any>();

        var currentDigit = "";
        var restartIndex: Int? = null;

        for (i in 0 until textView.text.length){
            val char = textView.text[i];

            if (restartIndex != null && i == restartIndex) {
                continue;
            }

            if(char.isDigit() || char == '.'){
                currentDigit += char;

            }else if(char == '('){
                currentDigit += textView.text[i + 1];
                restartIndex = i + 1;
                continue;

            }else if(char == ')'){
                continue;
            }else{
                list.add(currentDigit.toFloat());
                currentDigit = "";
                list.add(char);
            }
        }

        if (currentDigit != ""){
            list.add(currentDigit.toFloat())
        }

        return list;
    }





    fun positiveOrNegativeAction(view: View) {


        var result = positiveOrNegativeOnDigits();

        textView.text = result;
    }

    private fun positiveOrNegativeOnDigits(): String {
        var result = textView.text.toString();


        if (result.isEmpty()) {
            return "";
        }
        if (result.last() == ')' && !canDeleteParentheses) {
            val reversedResult = result.reversed();
            val modifiedResult = reversedResult.replaceFirst("(-", "", true).replaceFirst(")", "", true);
            val correctedResult = modifiedResult.reversed()
            canDeleteParentheses = true;
            return correctedResult;
        }


        if (!result.last().isDigit()) {
            return result;
        }

        if (result.isNotEmpty() && result.all { it.isDigit() }) {
            return "(-$result)";
        } else {
            var lastIndex = result.length;

            for (i in result.length - 1 downTo 0) {
                if (result[i] == ('+') || result[i] == ('-') || result[i] == ('/') || result[i] == ('x')) {
                    lastIndex = i;
                    break;
                }else if (result[i] == ('%')){
                    return result;
                }
            }

            val lastNumberString = result.substring(lastIndex + 1)
            val modifiedLastNumber = "(-$lastNumberString)"
            val modifiedResult = result.substring(0, lastIndex + 1) + modifiedLastNumber

            canDeleteParentheses = false;

            return modifiedResult;
        }
    }


}
