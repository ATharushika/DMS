import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList, ActivityIndicator } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import SubmitReportModal from '../../components/SubmitReportModal';
import axios from 'axios';

const VolunteerDashboard = () => {
  const { user, logout, API_URL } = useContext(AuthContext);
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);

  useEffect(() => {
    fetchMyReports();
  }, []);

  const fetchMyReports = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_URL}/hazards/my`);
      setReports(response.data);
    } catch (e) {
      console.log('Error fetching my reports', e);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = (id) => {
    Alert.alert('Confirm Delete', 'Are you sure you want to delete this report?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Delete', style: 'destructive', onPress: async () => {
        try {
          await axios.delete(`${API_URL}/hazards/${id}`);
          fetchMyReports();
        } catch(e) {
          Alert.alert('Error', 'Failed to delete report.');
        }
      }}
    ]);
  };

  const renderReport = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardTitle}>{item.hazardType}</Text>
        <Text style={[styles.badge, styles[`badge_${item.status}`] || styles.badge_DEFAULT]}>{item.status}</Text>
      </View>
      <Text style={styles.cardText}>{item.description}</Text>
      <View style={{flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginTop: 10}}>
        <TouchableOpacity style={styles.btnDelete} onPress={() => handleDelete(item.id)}>
          <Text style={styles.btnDeleteText}>Delete</Text>
        </TouchableOpacity>
        <Text style={styles.cardTime}>Reported: {new Date(item.reportedAt).toLocaleString()}</Text>
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Volunteer Dashboard</Text>
        <Text style={styles.headerSubtitle}>Welcome, {user?.name}</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>My Submitted Reports</Text>
        {loading ? (
          <ActivityIndicator size="large" color="#388e3c" />
        ) : (
          <FlatList
            data={reports}
            renderItem={renderReport}
            keyExtractor={(item) => item.id.toString()}
            ListEmptyComponent={<Text style={styles.emptyText}>You haven't submitted any reports yet.</Text>}
          />
        )}
      </View>

      <TouchableOpacity style={styles.fab} onPress={() => setModalVisible(true)}>
        <Text style={styles.fabText}>+ New Report</Text>
      </TouchableOpacity>

      <SubmitReportModal 
        visible={modalVisible} 
        onClose={() => setModalVisible(false)} 
        onSuccess={fetchMyReports} 
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#388e3c', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#c8e6c9', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  section: { flex: 1, padding: 15 },
  sectionTitle: { fontSize: 18, fontWeight: 'bold', color: '#333', marginBottom: 15 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 15, marginBottom: 15, elevation: 2 },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#333' },
  badge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12, fontSize: 10, fontWeight: 'bold', color: '#fff', overflow: 'hidden' },
  badge_DEFAULT: { backgroundColor: '#9e9e9e' },
  badge_SUBMITTED: { backgroundColor: '#f57c00' },
  badge_VERIFIED: { backgroundColor: '#388e3c' },
  cardText: { fontSize: 14, color: '#444', marginBottom: 10 },
  cardTime: { fontSize: 12, color: '#999', textAlign: 'right' },
  emptyText: { textAlign: 'center', color: '#666', fontStyle: 'italic', marginTop: 20 },
  fab: { position: 'absolute', bottom: 30, right: 20, backgroundColor: '#388e3c', paddingHorizontal: 20, paddingVertical: 15, borderRadius: 30, elevation: 5 },
  fabText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  btnDelete: { backgroundColor: '#ffebee', paddingHorizontal: 12, paddingVertical: 6, borderRadius: 5, borderWidth: 1, borderColor: '#ef9a9a' },
  btnDeleteText: { color: '#d32f2f', fontWeight: 'bold', fontSize: 12 }
});

export default VolunteerDashboard;
