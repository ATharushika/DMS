import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList, ActivityIndicator, Alert, Modal, ScrollView } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import CreateIncidentModal from '../../components/CreateIncidentModal';
import AllocateResourceModal from '../../components/AllocateResourceModal';
import axios from 'axios';

const DistrictOfficerDashboard = () => {
  const { logout, API_URL } = useContext(AuthContext);
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  
  // Modals
  const [assignModalVisible, setAssignModalVisible] = useState(false);
  const [incidentModalVisible, setIncidentModalVisible] = useState(false);
  const [resourceModalVisible, setResourceModalVisible] = useState(false);
  
  const [selectedIncident, setSelectedIncident] = useState(null);
  const [recommendedTeams, setRecommendedTeams] = useState([]);
  const [loadingTeams, setLoadingTeams] = useState(false);

  useEffect(() => {
    fetchIncidents();
  }, []);

  const fetchIncidents = async () => {
    try {
      const response = await axios.get(`${API_URL}/incidents`);
      const openIncidents = response.data.filter(i => i.status === 'OPEN' || i.status === 'IN_PROGRESS');
      setIncidents(openIncidents);
    } catch (e) {
      console.log('Error fetching incidents', e);
    } finally {
      setLoading(false);
    }
  };

  const openAssignModal = async (incident) => {
    setSelectedIncident(incident);
    setAssignModalVisible(true);
    setLoadingTeams(true);
    try {
      const response = await axios.get(`${API_URL}/rescue-teams/recommended?incidentId=${incident.id}`);
      setRecommendedTeams(response.data);
    } catch (e) {
      Alert.alert('Error', 'Failed to fetch recommended teams');
    } finally {
      setLoadingTeams(false);
    }
  };

  const assignTeam = async (teamId) => {
    try {
      await axios.post(`${API_URL}/rescue-assignments`, {
        incidentId: selectedIncident.id,
        teamId: teamId
      });
      Alert.alert('Success', 'Team dispatched successfully');
      setAssignModalVisible(false);
      fetchIncidents();
    } catch (e) {
      Alert.alert('Error', e.response?.data?.message || 'Failed to dispatch team');
    }
  };

  const handleDelete = (id) => {
    Alert.alert('Confirm Delete', 'Are you sure you want to delete this incident?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Delete', style: 'destructive', onPress: async () => {
        try {
          await axios.delete(`${API_URL}/incidents/${id}`);
          fetchIncidents();
        } catch(e) {
          Alert.alert('Error', 'Failed to delete incident. It may have active rescue assignments.');
        }
      }}
    ]);
  };

  const renderIncident = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardTitle}>Priority: {item.priority}</Text>
        <Text style={[styles.badge, styles[`badge_${item.status}`]]}>{item.status.replace('_', ' ')}</Text>
      </View>
      <Text style={styles.cardText}>Location: {item.locationName}</Text>
      <Text style={styles.cardText}>{item.description}</Text>
      
      <View style={styles.actionRow}>
        <TouchableOpacity style={styles.btnDelete} onPress={() => handleDelete(item.id)}>
          <Text style={styles.btnDeleteText}>Delete</Text>
        </TouchableOpacity>
        {item.status === 'OPEN' && (
          <TouchableOpacity style={styles.button} onPress={() => openAssignModal(item)}>
            <Text style={styles.buttonText}>Dispatch Rescue Team</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>District Officer Dashboard</Text>
        <Text style={styles.headerSubtitle}>Emergency Response Coordination</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>
      
      <View style={styles.quickActions}>
        <TouchableOpacity style={styles.quickBtn} onPress={() => setIncidentModalVisible(true)}>
          <Text style={styles.quickBtnText}>+ New Incident</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.quickBtn, {backgroundColor: '#f57c00'}]} onPress={() => setResourceModalVisible(true)}>
          <Text style={styles.quickBtnText}>+ Request Resources</Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#00695c" style={{ marginTop: 50 }} />
      ) : (
        <FlatList
          contentContainerStyle={styles.list}
          data={incidents}
          renderItem={renderIncident}
          keyExtractor={(item) => item.id.toString()}
          ListEmptyComponent={<Text style={styles.emptyText}>No open incidents.</Text>}
        />
      )}

      {/* Assignment Modal */}
      <Modal visible={assignModalVisible} transparent={true} animationType="slide">
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <Text style={styles.modalTitle}>Recommended Teams</Text>
            {loadingTeams ? (
              <ActivityIndicator color="#00695c" />
            ) : recommendedTeams.length === 0 ? (
              <Text>No available teams currently.</Text>
            ) : (
              <FlatList
                data={recommendedTeams}
                keyExtractor={t => t.id.toString()}
                renderItem={({ item }) => (
                  <View style={styles.teamCard}>
                    <View>
                      <Text style={styles.teamName}>{item.teamName}</Text>
                      <Text style={styles.teamSpec}>{item.specialization.replace(/_/g, ' ')}</Text>
                    </View>
                    <TouchableOpacity style={styles.btnAssign} onPress={() => assignTeam(item.id)}>
                      <Text style={styles.buttonText}>Assign</Text>
                    </TouchableOpacity>
                  </View>
                )}
              />
            )}
            <TouchableOpacity style={styles.btnClose} onPress={() => setAssignModalVisible(false)}>
              <Text style={styles.btnCloseText}>Close</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
      
      <CreateIncidentModal 
        visible={incidentModalVisible} 
        onClose={() => setIncidentModalVisible(false)} 
        onSuccess={fetchIncidents} 
      />
      
      <AllocateResourceModal 
        visible={resourceModalVisible} 
        onClose={() => setResourceModalVisible(false)} 
        onSuccess={() => {}} 
      />
      
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#00695c', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#b2dfdb', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  quickActions: { flexDirection: 'row', padding: 15, paddingBottom: 0, justifyContent: 'space-between' },
  quickBtn: { backgroundColor: '#00695c', flex: 1, padding: 12, borderRadius: 5, alignItems: 'center', marginHorizontal: 5, elevation: 2 },
  quickBtnText: { color: '#fff', fontWeight: 'bold' },
  list: { padding: 15 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 15, marginBottom: 15, elevation: 2 },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#d32f2f' },
  badge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12, fontSize: 10, fontWeight: 'bold', color: '#fff', overflow: 'hidden' },
  badge_OPEN: { backgroundColor: '#d32f2f' },
  badge_IN_PROGRESS: { backgroundColor: '#f57c00' },
  cardText: { fontSize: 14, color: '#444', marginBottom: 5 },
  actionRow: { flexDirection: 'row', marginTop: 15, justifyContent: 'flex-end', alignItems: 'center' },
  button: { backgroundColor: '#00695c', paddingHorizontal: 15, paddingVertical: 8, borderRadius: 5 },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 13 },
  btnDelete: { marginRight: 10, paddingHorizontal: 12, paddingVertical: 8, borderRadius: 5, backgroundColor: '#ffebee', borderWidth: 1, borderColor: '#ef9a9a' },
  btnDeleteText: { color: '#d32f2f', fontWeight: 'bold', fontSize: 13 },
  emptyText: { textAlign: 'center', marginTop: 50, color: '#666', fontStyle: 'italic' },
  
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalContent: { backgroundColor: '#fff', borderTopLeftRadius: 15, borderTopRightRadius: 15, padding: 20, maxHeight: '80%' },
  modalTitle: { fontSize: 18, fontWeight: 'bold', marginBottom: 15 },
  teamCard: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#f9f9f9', padding: 12, borderRadius: 8, marginBottom: 10, borderWidth: 1, borderColor: '#eee' },
  teamName: { fontWeight: 'bold', fontSize: 15 },
  teamSpec: { color: '#666', fontSize: 12 },
  btnAssign: { backgroundColor: '#1976d2', padding: 8, borderRadius: 5 },
  btnClose: { marginTop: 15, padding: 12, backgroundColor: '#eee', borderRadius: 5, alignItems: 'center' },
  btnCloseText: { fontWeight: 'bold', color: '#333' }
});

export default DistrictOfficerDashboard;
