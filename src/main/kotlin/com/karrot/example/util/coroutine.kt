package com.karrot.example.util

import com.karrot.example.repository.shipment.LastItemSubscriber
import io.reactivex.rxjava3.core.Maybe
import io.smallrye.mutiny.Multi
import reactor.core.publisher.Flux
import java.util.concurrent.CompletionStage
import java.util.concurrent.Flow
import kotlin.coroutines.Continuation

fun <T: Any> Maybe<T>.awaitSingle(cont: Continuation<Any>) {
    this.subscribe { user ->
        cont.resumeWith(Result.success(user))
    }
}

fun <T: Any> Flow.Publisher<T>.awaitLast(cont: Continuation<Any>) {
    this.subscribe(LastItemSubscriber { address ->
        cont.resumeWith(Result.success(address))
    })
}

fun <T: Any> Flux<T>.toList(cont: Continuation<Any>) {
    this.collectList()
        .subscribe { products ->
            cont.resumeWith(Result.success(products))
        }
}

fun <T: Any> Multi<T>.toList(cont: Continuation<Any>) {
    this.collect()
        .asList()
        .subscribeAsCompletionStage()
        .whenComplete { stores, _ ->
            cont.resumeWith(Result.success(stores))
        }
}

fun <T: Any> CompletionStage<T>.awaitSingle(cont: Continuation<Any>) {
    this.whenComplete { order, _ ->
        cont.resumeWith(Result.success(order))
    }
}
