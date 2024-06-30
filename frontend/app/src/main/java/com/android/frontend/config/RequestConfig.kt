package com.android.frontend.config

data class RequestConfig(
    val method: RequestMethod,
    val path: String,
    val headers: Map<String, String> = mapOf(),
    val query: Map<String, List<String>> = mapOf()
)

/*
Contiene tutte le informazioni necessarie per configurare e inviare una richiesta HTTP a un server,
inclusi il metodo, il percorso dell'endpoint, le intestazioni e i parametri di query
 */