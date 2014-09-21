package com.home.vkmusicloader.services;

import android.os.Binder;

public class LocalBinder<T> extends Binder {
	private final T m_Service;
	public LocalBinder(T service){
		m_Service = service;
	}
       public  T getService() {
            return m_Service;
        }
}
