import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, FlatList, ActivityIndicator, Alert } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import axios from 'axios';

const DutyOfficerDashboard = () => {
  const { user, logout, API_URL } = useContext(AuthContext);
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchReports();
  }, []);

  const fetchReports = async () => {
    try {
      const response = await axios.get(`${API_URL}/hazards`);
      // Show reports that need attention
      const pending = response.data.filter(r => 
        r.status === 'SUBMITTED' || r.status === 'UNDER_REVIEW' || r.status === 'MORE_INFORMATION_REQUIRED'
      );
      setReports(pending);
    } catch (e) {
      console.log('Error fetching reports', e);
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (id, action) => {
    try {
      if (action === 'review') {
        await axios.post(`${API_URL}/hazards/${id}/review`);
      } else if (action === 'verify') {
        await axios.post(`${API_URL}/hazards/${id}/verify`, { comments: 'Verified by Duty Officer' });
      } else if (action === 'reject') {
        await axios.post(`${API_URL}/hazards/${id}/reject`, { comments: 'Rejected by Duty Officer' });
      }
      fetchReports();
      Alert.alert('Success', `Report ${action} successful`);
    } catch (e) {
      Alert.alert('Error', `Failed to ${action} report`);
    }
  };

  const renderReport = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardTitle}>{item.hazardType}</Text>
        <Text style={[styles.badge, styles[`badge_${item.status}`]]}>{item.status}</Text>
      </View>
      <Text style={styles.cardText}>Location: {item.locationName}</Text>
      <Text style={styles.cardText}>{item.description}</Text>
      <Text style={styles.cardSubText}>Credibility Score: {item.credibilityScore}%</Text>
      <Text style={styles.cardSubText}>Reported: {new Date(item.reportedAt).toLocaleString()}</Text>

      <View style={styles.actionRow}>
        {item.status === 'SUBMITTED' && (
          <TouchableOpacity style={[styles.button, styles.btnReview]} onPress={() => handleAction(item.id, 'review')}>
            <Text style={styles.buttonText}>Review</Text>
          </TouchableOpacity>
        )}
        {(item.status === 'UNDER_REVIEW' || item.status === 'MORE_INFORMATION_REQUIRED') && (
          <>
            <TouchableOpacity style={[styles.button, styles.btnVerify]} onPress={() => handleAction(item.id, 'verify')}>
              <Text style={styles.buttonText}>Verify</Text>
            </TouchableOpacity>
            <TouchableOpacity style={[styles.button, styles.btnReject]} onPress={() => handleAction(item.id, 'reject')}>
              <Text style={styles.buttonText}>Reject</Text>
            </TouchableOpacity>
          </>
        )}
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Duty Officer Dashboard</Text>
        <Text style={styles.headerSubtitle}>Verify Ground Hazard Reports</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#1a73e8" style={{ marginTop: 50 }} />
      ) : (
        <FlatList
          contentContainerStyle={styles.list}
          data={reports}
          renderItem={renderReport}
          keyExtractor={(item) => item.id.toString()}
          ListEmptyComponent={<Text style={styles.emptyText}>No pending reports to review.</Text>}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#1a73e8', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#e0e0e0', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  list: { padding: 15 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 15, marginBottom: 15, elevation: 2 },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  cardTitle: { fontSize: 18, fontWeight: 'bold', color: '#333' },
  badge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12, fontSize: 10, fontWeight: 'bold', color: '#fff', overflow: 'hidden' },
  badge_SUBMITTED: { backgroundColor: '#f57c00' },
  badge_UNDER_REVIEW: { backgroundColor: '#1976d2' },
  badge_MORE_INFORMATION_REQUIRED: { backgroundColor: '#7b1fa2' },
  cardText: { fontSize: 14, color: '#444', marginBottom: 5 },
  cardSubText: { fontSize: 12, color: '#888', marginTop: 5 },
  actionRow: { flexDirection: 'row', marginTop: 15, justifyContent: 'flex-end', gap: 10 },
  button: { paddingHorizontal: 15, paddingVertical: 8, borderRadius: 5, marginLeft: 10 },
  btnReview: { backgroundColor: '#1976d2' },
  btnVerify: { backgroundColor: '#388e3c' },
  btnReject: { backgroundColor: '#d32f2f' },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 13 },
  emptyText: { textAlign: 'center', marginTop: 50, color: '#666', fontStyle: 'italic' }
});

export default DutyOfficerDashboard;
