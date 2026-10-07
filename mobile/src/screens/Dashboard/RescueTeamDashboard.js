import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList, ActivityIndicator, Alert } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import axios from 'axios';

const RescueTeamDashboard = () => {
  const { user, logout, API_URL } = useContext(AuthContext);
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [teamId, setTeamId] = useState(null);

  useEffect(() => {
    initTeamData();
  }, []);

  const initTeamData = async () => {
    try {
      // Find my team ID
      const teamsRes = await axios.get(`${API_URL}/rescue-teams`);
      const myTeam = teamsRes.data.find(t => t.user?.id === user.userId);
      
      if (myTeam) {
        setTeamId(myTeam.id);
        fetchAssignments(myTeam.id);
      } else {
        setLoading(false);
        Alert.alert('Error', 'Rescue team profile not found.');
      }
    } catch (e) {
      console.log('Error init team', e);
      setLoading(false);
    }
  };

  const fetchAssignments = async (id) => {
    try {
      const response = await axios.get(`${API_URL}/rescue-assignments?teamId=${id}`);
      // Show active assignments
      const active = response.data.filter(a => a.status !== 'COMPLETED' && a.status !== 'CANCELLED');
      setAssignments(active);
    } catch (e) {
      console.log('Error fetching assignments', e);
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (assignmentId, action) => {
    let url = '';
    let body = {};
    if (action === 'accept') {
      url = `${API_URL}/rescue-assignments/${assignmentId}/accept`;
      await axios.put(url);
    } else {
      url = `${API_URL}/rescue-assignments/${assignmentId}/status`;
      await axios.put(url, { status: action });
    }
    fetchAssignments(teamId);
  };

  const renderAssignment = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardTitle}>Incident Dispatch</Text>
        <Text style={[styles.badge, styles[`badge_${item.status}`]]}>{item.status.replace('_', ' ')}</Text>
      </View>
      <Text style={styles.cardText}>Location: {item.incident?.locationName}</Text>
      <Text style={styles.cardText}>{item.incident?.description}</Text>
      
      <View style={styles.actionRow}>
        {item.status === 'ASSIGNED' && (
          <TouchableOpacity style={styles.button} onPress={() => updateStatus(item.id, 'accept')}>
            <Text style={styles.buttonText}>Accept Assignment</Text>
          </TouchableOpacity>
        )}
        {item.status === 'ACCEPTED' && (
          <TouchableOpacity style={[styles.button, styles.btnEnRoute]} onPress={() => updateStatus(item.id, 'EN_ROUTE')}>
            <Text style={styles.buttonText}>Start Journey</Text>
          </TouchableOpacity>
        )}
        {item.status === 'EN_ROUTE' && (
          <TouchableOpacity style={[styles.button, styles.btnOnSite]} onPress={() => updateStatus(item.id, 'ON_SITE')}>
            <Text style={styles.buttonText}>Arrived On Site</Text>
          </TouchableOpacity>
        )}
        {item.status === 'ON_SITE' && (
          <TouchableOpacity style={[styles.button, styles.btnComplete]} onPress={() => updateStatus(item.id, 'COMPLETED')}>
            <Text style={styles.buttonText}>Mark Completed</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Rescue Team App</Text>
        <Text style={styles.headerSubtitle}>Active Deployments</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#d32f2f" style={{ marginTop: 50 }} />
      ) : (
        <FlatList
          contentContainerStyle={styles.list}
          data={assignments}
          renderItem={renderAssignment}
          keyExtractor={(item) => item.id.toString()}
          ListEmptyComponent={<Text style={styles.emptyText}>No active assignments. Stand by.</Text>}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#c62828', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#ffcdd2', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  list: { padding: 15 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 15, marginBottom: 15, elevation: 2, borderLeftWidth: 5, borderLeftColor: '#c62828' },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#333' },
  badge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12, fontSize: 10, fontWeight: 'bold', color: '#fff', overflow: 'hidden' },
  badge_ASSIGNED: { backgroundColor: '#f57c00' },
  badge_ACCEPTED: { backgroundColor: '#1976d2' },
  badge_EN_ROUTE: { backgroundColor: '#7b1fa2' },
  badge_ON_SITE: { backgroundColor: '#c62828' },
  cardText: { fontSize: 14, color: '#444', marginBottom: 5 },
  actionRow: { flexDirection: 'row', marginTop: 15, justifyContent: 'flex-end' },
  button: { backgroundColor: '#1976d2', paddingHorizontal: 15, paddingVertical: 8, borderRadius: 5 },
  btnEnRoute: { backgroundColor: '#7b1fa2' },
  btnOnSite: { backgroundColor: '#c62828' },
  btnComplete: { backgroundColor: '#388e3c' },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 13 },
  emptyText: { textAlign: 'center', marginTop: 50, color: '#666', fontStyle: 'italic' }
});

export default RescueTeamDashboard;
