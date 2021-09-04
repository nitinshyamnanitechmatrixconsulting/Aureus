package com.auresus.academy.di.koin


import com.auresus.academy.view.forgotpassword.ForgotPasswordViewModel
import com.auresus.academy.view.invoice.InvoiceViewModel
import com.auresus.academy.view.login.LoginViewModel
import com.auresus.academy.view.notification.NotificationViewModel
import com.auresus.academy.view.studenthome.MakeUpViewModel
import com.auresus.academy.view.studenthome.home.HomeViewModel
import com.auresus.academy.view.studenthome.home.ReferViewModel
import com.auresus.academy.view.studenthome.settings.StudentViewModel
import com.auresus.academy.view.studenthome.ticket.TicketViewModel
import com.auresus.academy.view_model.BaseViewModel
import com.auresus.academy.view_model.DashboardViewModel
import com.auresus.academy.view_model.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


/** Created by Sahil Bharti on 5/4/19.
 *
 * Copyright (c) 2019 Sahil Inc. All rights reserved.
 */

val viewModelModule = module {

    /**Provide ViewModel object in activity Class
     * you can use it any Activity/Fragment class  below is declaration
     *
     * In Activity
     * private val baseViewModel: BaseViewModel by viewmodel() create object in activity scope
     *
     * In Fragment
     *  private val baseViewModel: BaseViewModel by viewmodel()  create object in fragment scope
     *
     *  get object of activity scope use sharedViewModel()
     *  private val baseViewModel: BaseViewModel by sharedViewmodel()
     *  */

    viewModel { BaseViewModel() }
    viewModel { SplashViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { InvoiceViewModel(get()) }
    viewModel { TicketViewModel(get()) }
    viewModel { StudentViewModel(get()) }
    viewModel { MakeUpViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ReferViewModel(get()) }

}