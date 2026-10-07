import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList, ActivityIndicator, Alert, ScrollView } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import CreateWarningModal from '../../components/CreateWarningModal';
import axios from 'axios';

const DMCOfficerDashboard = () => {
  const { logout, API_URL } = useContext(AuthContext);
  const [warnings, setWarnings] = useState([]);
  const [verifiedHazards, setVerifiedHazards] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedHazard, setSelectedHazard] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const wRes = await axios.get(`${API_URL}/warnings`);
      const activeOrPending = wRes.data.filter(w => w.status !== 'EXPIRED' && w.status !== 'CANCELLED');
      setWarnings(activeOrPending);

      const hRes = await axios.get(`${API_URL}/hazards?status=VERIFIED`);
      
      // Filter out hazards that have already been converted into a warning
      const usedHazardIds = wRes.data.map(w => w.sourceReport?.id).filter(id => id != null);
      const availableHazards = hRes.data.filter(h => !usedHazardIds.includes(h.id));
      
      setVerifiedHazards(availableHazards);
    } catch (e) {
      console.log('Error fetching DMC data', e);
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (id, action) => {
    try {
      if (action === 'approve') await axios.post(`${API_URL}/warnings/${id}/approve`);
      else if (action === 'submit-approval') await axios.post(`${API_URL}/warnings/${id}/submit-approval`);
      else if (action === 'broadcast') await axios.post(`${API_URL}/warnings/${id}/broadcast`);
      else if (action === 'deactivate') await axios.post(`${API_URL}/warnings/${id}/deactivate`);
      else if (action === 'delete') await axios.delete(`${API_URL}/warnings/${id}`);
      
      fetchData();
      Alert.alert('Success', `Warning ${action} successful`);
    } catch (e) {
      Alert.alert('Error', e.response?.data?.message || `Failed to ${action} warning`);
    }
  };

  const confirmDelete = (id) => {
    Alert.alert('Confirm Delete', 'Are you sure you want to delete this warning?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Delete', style: 'destructive', onPress: () => handleAction(id, 'delete') }
    ]);
  };

  const openCreateModal = (hazard = null) => {
    setSelectedHazard(hazard);
    setModalVisible(true);
  };

  const renderHazard = ({ item }) => (
    <View style={styles.hazardCard}>
      <Text style={styles.cardTitle}>{item.hazardType} at {item.locationName}</Text>
      <Text style={styles.cardText}>{item.description}</Text>
      <TouchableOpacity style={styles.btnCreateWarning} onPress={() => openCreateModal(item)}>
        <Text style={styles.buttonText}>Issue Warning for this Hazard</Text>
      </TouchableOpacity>
    </View>
  );

  const renderWarning = ({ item }) => (
    <View style={[styles.card, item.warningLevel === 'CRITICAL' ? styles.cardCritical : null]}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardTitle}>{item.hazardType} - {item.warningLevel}</Text>
        <Text style={[styles.badge, styles[`badge_${item.status}`]]}>{item.status.replace('_', ' ')}</Text>
      </View>
      <Text style={styles.cardText}>Area: {item.affectedArea}</Text>
      <Text style={styles.cardText}>{item.message}</Text>
      
      <View style={styles.actionRow}>
        {item.status === 'DRAFT' && item.warningLevel === 'CRITICAL' && (
          <TouchableOpacity style={[styles.button, styles.btnWarn]} onPress={() => handleAction(item.id, 'submit-approval')}>
            <Text style={styles.buttonText}>Request Approval</Text>
          </TouchableOpacity>
        )}
        {(item.status === 'DRAFT' && item.warningLevel !== 'CRITICAL') && (
          <TouchableOpacity style={[styles.button, styles.btnApprove]} onPress={() => handleAction(item.id, 'approve')}>
            <Text style={styles.buttonText}>Approve</Text>
          </TouchableOpacity>
        )}
        {item.status === 'PENDING_APPROVAL' && (
          <TouchableOpacity style={[styles.button, styles.btnApprove]} onPress={() => handleAction(item.id, 'approve')}>
            <Text style={styles.buttonText}>Authorize</Text>
          </TouchableOpacity>
        )}
        {item.status === 'APPROVED' && (
          <TouchableOpacity style={[styles.button, styles.btnBroadcast]} onPress={() => handleAction(item.id, 'broadcast')}>
            <Text style={styles.buttonText}>Broadcast to Citizens</Text>
          </TouchableOpacity>
        )}
        {item.status === 'ACTIVE' && (
          <TouchableOpacity style={[styles.button, styles.btnDeactivate]} onPress={() => handleAction(item.id, 'deactivate')}>
            <Text style={styles.buttonText}>Deactivate</Text>
          </TouchableOpacity>
        )}
        <TouchableOpacity style={[styles.button, styles.btnDelete]} onPress={() => confirmDelete(item.id)}>
          <Text style={styles.buttonText}>Delete</Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>DMC Dashboard</Text>
        <Text style={styles.headerSubtitle}>Manage Disaster Warnings</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#d84315" style={{ marginTop: 50 }} />
      ) : (
        <View style={styles.content}>
          <Text style={styles.sectionTitle}>Verified Hazards (Ready for Warning)</Text>
          {verifiedHazards.length === 0 ? (
            <Text style={styles.emptyText}>No verified hazards waiting.</Text>
          ) : (
            <FlatList
              data={verifiedHazards}
              renderItem={renderHazard}
              keyExtractor={(item) => item.id.toString()}
              scrollEnabled={false}
            />
          )}

          <View style={styles.divider} />
          
          <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 15 }}>
             <Text style={[styles.sectionTitle, {marginBottom: 0}]}>Active & Draft Warnings</Text>
             <TouchableOpacity style={styles.btnSmall} onPress={() => openCreateModal()}>
                <Text style={styles.buttonText}>+ Custom Warning</Text>
             </TouchableOpacity>
          </View>
          
          {warnings.length === 0 ? (
            <Text style={styles.emptyText}>No warnings found.</Text>
          ) : (
            <FlatList
              data={warnings}
              renderItem={renderWarning}
              keyExtractor={(item) => item.id.toString()}
              scrollEnabled={false}
            />
          )}
        </View>
      )}

      <CreateWarningModal 
        visible={modalVisible} 
        onClose={() => setModalVisible(false)} 
        onSuccess={fetchData} 
        sourceHazard={selectedHazard} 
      />
      <View style={{ height: 40 }} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#d84315', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#ffccbc', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  content: { padding: 15 },
  sectionTitle: { fontSize: 18, fontWeight: 'bold', color: '#333', marginBottom: 15 },
  divider: { height: 1, backgroundColor: '#ddd', marginVertical: 20 },
  hazardCard: { backgroundColor: '#e3f2fd', padding: 15, borderRadius: 8, marginBottom: 10, borderLeftWidth: 4, borderLeftColor: '#1976d2' },
  btnCreateWarning: { backgroundColor: '#d84315', padding: 10, borderRadius: 5, alignItems: 'center', marginTop: 10 },
  btnSmall: { backgroundColor: '#d84315', padding: 8, borderRadius: 5 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 15, marginBottom: 15, elevation: 2, borderLeftWidth: 5, borderLeftColor: '#ff9800' },
  cardCritical: { borderLeftColor: '#d32f2f' },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#333', flex: 1 },
  badge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12, fontSize: 9, fontWeight: 'bold', color: '#fff', overflow: 'hidden' },
  badge_DRAFT: { backgroundColor: '#9e9e9e' },
  badge_PENDING_APPROVAL: { backgroundColor: '#f57c00' },
  badge_APPROVED: { backgroundColor: '#388e3c' },
  badge_ACTIVE: { backgroundColor: '#d32f2f' },
  cardText: { fontSize: 14, color: '#444', marginBottom: 5 },
  actionRow: { flexDirection: 'row', marginTop: 15, justifyContent: 'flex-end', flexWrap: 'wrap', gap: 5 },
  button: { paddingHorizontal: 15, paddingVertical: 8, borderRadius: 5, marginLeft: 5, marginBottom: 5 },
  btnWarn: { backgroundColor: '#f57c00' },
  btnApprove: { backgroundColor: '#388e3c' },
  btnBroadcast: { backgroundColor: '#1976d2' },
  btnDeactivate: { backgroundColor: '#7b1fa2' },
  btnDelete: { backgroundColor: '#d32f2f' },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 13 },
  emptyText: { textAlign: 'center', marginVertical: 20, color: '#666', fontStyle: 'italic' }
});

export default DMCOfficerDashboard;
