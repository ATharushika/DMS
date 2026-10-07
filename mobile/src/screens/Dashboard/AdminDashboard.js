import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, ActivityIndicator, Modal, FlatList, Alert } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import axios from 'axios';

const AdminDashboard = () => {
  const { logout, API_URL } = useContext(AuthContext);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  
  const [usersModal, setUsersModal] = useState(false);
  const [logsModal, setLogsModal] = useState(false);
  
  const [users, setUsers] = useState([]);
  const [logs, setLogs] = useState([]);
  const [loadingList, setLoadingList] = useState(false);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const response = await axios.get(`${API_URL}/dashboard/stats`);
      setStats(response.data);
    } catch (e) {
      console.log('Error fetching stats', e);
    } finally {
      setLoading(false);
    }
  };

  const openUsers = async () => {
    setUsersModal(true);
    setLoadingList(true);
    try {
      const response = await axios.get(`${API_URL}/users`);
      setUsers(response.data);
    } catch (e) {
      Alert.alert('Error', 'Failed to fetch users');
    } finally {
      setLoadingList(false);
    }
  };

  const openLogs = async () => {
    setLogsModal(true);
    setLoadingList(true);
    try {
      const response = await axios.get(`${API_URL}/audit-logs`);
      setLogs(response.data);
    } catch (e) {
      Alert.alert('Error', 'Failed to fetch logs');
    } finally {
      setLoadingList(false);
    }
  };

  const toggleUserStatus = async (user) => {
    try {
      await axios.put(`${API_URL}/users/${user.id}`, { active: !user.active });
      // Refresh list
      const response = await axios.get(`${API_URL}/users`);
      setUsers(response.data);
    } catch (e) {
      Alert.alert('Error', 'Failed to update user');
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>System Admin</Text>
        <Text style={styles.headerSubtitle}>SDEWEC Overview</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        {loading ? (
          <ActivityIndicator size="large" color="#37474f" style={{ marginTop: 50 }} />
        ) : (
          <View style={styles.statsGrid}>
            <View style={styles.statCard}>
              <Text style={styles.statValue}>{stats?.activeWarnings || 0}</Text>
              <Text style={styles.statLabel}>Active Warnings</Text>
            </View>
            <View style={styles.statCard}>
              <Text style={[styles.statValue, { color: '#d32f2f' }]}>{stats?.criticalWarnings || 0}</Text>
              <Text style={styles.statLabel}>Critical Warnings</Text>
            </View>
            <View style={styles.statCard}>
              <Text style={styles.statValue}>{stats?.pendingReports || 0}</Text>
              <Text style={styles.statLabel}>Pending Reports</Text>
            </View>
            <View style={styles.statCard}>
              <Text style={styles.statValue}>{stats?.openIncidents || 0}</Text>
              <Text style={styles.statLabel}>Open Incidents</Text>
            </View>
            <View style={styles.statCard}>
              <Text style={styles.statValue}>{stats?.availableTeams || 0}</Text>
              <Text style={styles.statLabel}>Available Teams</Text>
            </View>
            <View style={styles.statCard}>
              <Text style={styles.statValue}>{stats?.totalUsers || 0}</Text>
              <Text style={styles.statLabel}>Total Users</Text>
            </View>
          </View>
        )}
        
        <View style={styles.adminActions}>
          <Text style={styles.sectionTitle}>Management Links</Text>
          <TouchableOpacity style={styles.linkCard} onPress={openUsers}>
            <Text style={styles.linkText}>User Management</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.linkCard} onPress={openLogs}>
            <Text style={styles.linkText}>System Audit Logs</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.linkCard} onPress={() => Alert.alert('Database Settings', 'Configured securely via application.properties')}>
            <Text style={styles.linkText}>Database Settings</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>

      {/* Users Modal */}
      <Modal visible={usersModal} animationType="slide" onRequestClose={() => setUsersModal(false)}>
        <View style={styles.modalContainer}>
          <Text style={styles.modalTitle}>User Management</Text>
          {loadingList ? <ActivityIndicator size="large" /> : (
            <FlatList
              data={users}
              keyExtractor={u => u.id.toString()}
              renderItem={({ item }) => (
                <View style={styles.listItem}>
                  <View>
                    <Text style={{fontWeight: 'bold'}}>{item.name} ({item.role})</Text>
                    <Text style={{color: '#666', fontSize: 12}}>{item.email}</Text>
                  </View>
                  <TouchableOpacity 
                    style={[styles.statusBtn, item.active ? styles.btnDeactivate : styles.btnActivate]} 
                    onPress={() => toggleUserStatus(item)}
                  >
                    <Text style={{color: '#fff', fontSize: 12}}>{item.active ? 'Deactivate' : 'Activate'}</Text>
                  </TouchableOpacity>
                </View>
              )}
            />
          )}
          <TouchableOpacity style={styles.closeBtn} onPress={() => setUsersModal(false)}><Text style={styles.closeBtnText}>Close</Text></TouchableOpacity>
        </View>
      </Modal>

      {/* Logs Modal */}
      <Modal visible={logsModal} animationType="slide" onRequestClose={() => setLogsModal(false)}>
        <View style={styles.modalContainer}>
          <Text style={styles.modalTitle}>System Audit Logs</Text>
          {loadingList ? <ActivityIndicator size="large" /> : (
            <FlatList
              data={logs}
              keyExtractor={l => l.id.toString()}
              renderItem={({ item }) => (
                <View style={styles.listItem}>
                  <View>
                    <Text style={{fontWeight: 'bold', color: '#1976d2'}}>{item.action}</Text>
                    <Text style={{color: '#333', fontSize: 13}}>By: {item.user?.name} on {item.entityType} #{item.entityId}</Text>
                    <Text style={{color: '#666', fontSize: 11}}>{new Date(item.timestamp).toLocaleString()}</Text>
                  </View>
                </View>
              )}
            />
          )}
          <TouchableOpacity style={styles.closeBtn} onPress={() => setLogsModal(false)}><Text style={styles.closeBtnText}>Close</Text></TouchableOpacity>
        </View>
      </Modal>

    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#eceff1' },
  header: { backgroundColor: '#37474f', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#cfd8dc', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  content: { padding: 15 },
  statsGrid: { flexDirection: 'row', flexWrap: 'wrap', justifyContent: 'space-between' },
  statCard: { width: '48%', backgroundColor: '#fff', padding: 20, borderRadius: 8, alignItems: 'center', marginBottom: 15, elevation: 2 },
  statValue: { fontSize: 28, fontWeight: 'bold', color: '#1976d2' },
  statLabel: { fontSize: 12, color: '#666', marginTop: 5, textAlign: 'center' },
  adminActions: { marginTop: 20 },
  sectionTitle: { fontSize: 18, fontWeight: 'bold', color: '#333', marginBottom: 10 },
  linkCard: { backgroundColor: '#fff', padding: 15, borderRadius: 5, marginBottom: 10, borderLeftWidth: 4, borderLeftColor: '#37474f' },
  linkText: { fontWeight: 'bold', color: '#333' },
  
  modalContainer: { flex: 1, padding: 20, backgroundColor: '#f9f9f9', paddingTop: 50 },
  modalTitle: { fontSize: 20, fontWeight: 'bold', marginBottom: 20 },
  listItem: { backgroundColor: '#fff', padding: 15, borderRadius: 5, marginBottom: 10, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', elevation: 1 },
  statusBtn: { padding: 8, borderRadius: 5 },
  btnDeactivate: { backgroundColor: '#d32f2f' },
  btnActivate: { backgroundColor: '#388e3c' },
  closeBtn: { marginTop: 20, padding: 15, backgroundColor: '#37474f', borderRadius: 5, alignItems: 'center' },
  closeBtnText: { color: '#fff', fontWeight: 'bold' }
});

export default AdminDashboard;
