package com.auresus.academy.di.koin

import com.auresus.academy.model.repo.AppRepository
import com.auresus.academy.model.repo.AuthenticationRepository
import com.auresus.academy.model.repo.ContactRepository
import com.auresus.academy.model.repo.StudentHomeRepository
import org.koin.dsl.module


/** Created by Sahil Bharti on 5/4/19.
 *
 * Copyright (c) 2019 Sahil Inc. All rights reserved.
*/


val repoModule = module {

    /**Provide ContactRepository class Singleton object
     * you can use it any KoinComponent class  below is declaration
     *  private val globalRepository: ContactRepository by inject() */

    single { ContactRepository(get(), get(), get()) }
    single { StudentHomeRepository(get(), get(), get()) }
    single { AuthenticationRepository(get(), get(), get()) }
    single { AppRepository(get(),get(),get()) }

}