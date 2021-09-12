package com.jayesh.dindinntest.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class CoroutinesAsync<Params,Progress,Result> {
    open fun onPreExecute(){

    }

    abstract fun doInBackground(vararg params:Params):Result

    open fun OnProgressUpdate(vararg progress:Progress?){}

    open fun OnPostExecute(result: Result){

    }

    open fun OnCancelled(result: Result){

    }
    protected fun publishProgress(vararg progress: Progress){
        GlobalScope.launch(Dispatchers.Main){
            OnProgressUpdate(*progress)
        }
    }

    fun execute(vararg params: Params){
        GlobalScope.launch(Dispatchers.Default){
            val result = doInBackground(*params)
            withContext(Dispatchers.Main){
                OnPostExecute(result)
            }
        }
    }

    fun cancel(mayInterruptIfRunning:Boolean){

    }
}