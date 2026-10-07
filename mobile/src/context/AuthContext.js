import React, { createContext, useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  // Replace with your local machine's IP address when testing on physical device
  const API_URL = 'http://192.168.8.147:8080/api';

  useEffect(() => {
    checkLoginStatus();
  }, []);

  const checkLoginStatus = async () => {
    try {
      const storedUser = await AsyncStorage.getItem('user');
      const token = await AsyncStorage.getItem('token');
      
      if (storedUser && token) {
        setUser(JSON.parse(storedUser));
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      }
    } catch (e) {
      console.log('Failed to fetch user state', e);
    } finally {
      setIsLoading(false);
    }
  };

  const login = async (email, password) => {
    setError('');
    try {
      const response = await axios.post(`${API_URL}/auth/login`, {
        email,
        password
      });

      if (response.data && response.data.token) {
        const { token, ...userData } = response.data;
        await AsyncStorage.setItem('token', token);
        await AsyncStorage.setItem('user', JSON.stringify(userData));
        
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        setUser(userData);
        return true;
      }
    } catch (e) {
      setError(e.response?.data?.message || 'Login failed. Please check your credentials.');
      return false;
    }
  };

  const register = async (userData) => {
    setError('');
    try {
      const response = await axios.post(`${API_URL}/auth/register`, userData);
      
      if (response.data && response.data.token) {
        const { token, ...newUserData } = response.data;
        await AsyncStorage.setItem('token', token);
        await AsyncStorage.setItem('user', JSON.stringify(newUserData));
        
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        setUser(newUserData);
        return true;
      }
    } catch (e) {
      setError(e.response?.data?.message || 'Registration failed.');
      return false;
    }
  };

  const logout = async () => {
    try {
      await AsyncStorage.removeItem('token');
      await AsyncStorage.removeItem('user');
      delete axios.defaults.headers.common['Authorization'];
      setUser(null);
    } catch (e) {
      console.log('Failed to logout', e);
    }
  };

  return (
    <AuthContext.Provider value={{ user, isLoading, error, login, register, logout, API_URL }}>
      {children}
    </AuthContext.Provider>
  );
};
