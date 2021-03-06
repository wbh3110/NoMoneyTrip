package com.mtjin.nomoneytrip.views.master_main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mtjin.nomoneytrip.base.BaseViewModel
import com.mtjin.nomoneytrip.data.master_main.MasterProduct
import com.mtjin.nomoneytrip.data.master_main.source.MasterMainRepository
import com.mtjin.nomoneytrip.utils.SingleLiveEvent
import com.mtjin.nomoneytrip.utils.TAG
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class MasterMainViewModel(private val repository: MasterMainRepository) : BaseViewModel() {

    private val _newProductList = MutableLiveData<ArrayList<MasterProduct>>()
    private val _acceptedProductList = MutableLiveData<ArrayList<MasterProduct>>()
    private val _onClickAlarm = SingleLiveEvent<Unit>()

    val newProductList: LiveData<ArrayList<MasterProduct>> get() = _newProductList
    val acceptedProductList: LiveData<ArrayList<MasterProduct>> get() = _acceptedProductList
    val onClickAlarm: LiveData<Unit> get() = _onClickAlarm

    fun requestNewMasterProducts() {
        compositeDisposable.add(
            repository.requestNewMasterProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = {
                        Log.d(TAG, it.toString())
                    },
                    onSuccess = {
                        _newProductList.value = it as ArrayList<MasterProduct>
                    }
                )
        )
    }

    fun requestAcceptedMasterProducts() {
        compositeDisposable.add(
            repository.requestAcceptedMasterProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = {
                        Log.d(TAG, it.toString())
                    },
                    onSuccess = {
                        _acceptedProductList.value = it as ArrayList<MasterProduct>
                    }
                )
        )
    }

    fun updateReservationState(masterProduct: MasterProduct, masterState: Int) {
        compositeDisposable.add(
            repository.updateReservationState(
                masterProduct = masterProduct,
                masterState = masterState
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgress() }
                .doAfterTerminate { hideProgress() }
                .subscribeBy(
                    onError = {
                        Log.d(TAG, it.toString())
                    },
                    onComplete = {
                        requestNewMasterProducts()
                        requestAcceptedMasterProducts()
                    }
                )
        )
    }

    fun onClickAlarm(){
        _onClickAlarm.call()
    }
}