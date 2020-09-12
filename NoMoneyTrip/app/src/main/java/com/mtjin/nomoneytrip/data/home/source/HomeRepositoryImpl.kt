package com.mtjin.nomoneytrip.data.home.source

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mtjin.nomoneytrip.data.home.Product
import com.mtjin.nomoneytrip.utils.FAVORITE
import com.mtjin.nomoneytrip.utils.FAVORITE_LIST
import com.mtjin.nomoneytrip.utils.PRODUCT
import com.mtjin.nomoneytrip.utils.uuid
import io.reactivex.Completable
import io.reactivex.Single

class HomeRepositoryImpl(private val database: DatabaseReference) : HomeRepository {
    override fun requestProducts(): Single<List<Product>> {
        return Single.create { emitter ->
            database.child(PRODUCT).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val productList = ArrayList<Product>()
                    for (productSnapShot: DataSnapshot in snapshot.children) {
                        productSnapShot.getValue(Product::class.java)?.let {
                            productList.add(it)
                        }
                    }
                    emitter.onSuccess(productList)
                }

            })
        }
    }

    override fun requestHashTagProducts(hashTag: String): Single<List<Product>> {
        return Single.create { emitter ->
            database.child(PRODUCT).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    emitter.onError(error.toException())
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val productList = ArrayList<Product>()
                    for (productSnapShot: DataSnapshot in snapshot.children) {
                        productSnapShot.getValue(Product::class.java)?.let {
                            if (it.hashTagList.contains(hashTag)) {
                                productList.add(it)
                            }
                        }
                    }
                    emitter.onSuccess(productList)
                }

            })
        }
    }

    override fun updateProductFavorite(product: Product): Completable {
        return Completable.create { emitter ->
            val updateMap = HashMap<String, Any>()
            updateMap.put(FAVORITE_LIST, product.favoriteList)
            database.child(PRODUCT).child(product.id).updateChildren(updateMap)
                .addOnSuccessListener {
                    database.child(FAVORITE).child(uuid).child(product.id).setValue(product.id)
                        .addOnSuccessListener {
                            emitter.onComplete()
                        }.addOnFailureListener {
                        emitter.onError(it)
                    }
                }.addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }
}