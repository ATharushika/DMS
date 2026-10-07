import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList, ActivityIndicator, Alert, TextInput } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import axios from 'axios';

const ShelterDashboard = () => {
  const { logout, API_URL } = useContext(AuthContext);
  const [shelter, setShelter] = useState(null);
  const [allocations, setAllocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [occupancyInput, setOccupancyInput] = useState('');

  useEffect(() => {
    fetchShelterData();
  }, []);

  const fetchShelterData = async () => {
    try {
      const shRes = await axios.get(`${API_URL}/shelters/my`);
      setShelter(shRes.data);
      setOccupancyInput(shRes.data.currentOccupancy.toString());
      
      const alRes = await axios.get(`${API_URL}/resource-allocations?shelterId=${shRes.data.id}`);
      setAllocations(alRes.data.filter(a => a.status === 'DISPATCHED' || a.status === 'RECEIVED'));
    } catch (e) {
      console.log('Error fetching shelter data', e);
    } finally {
      setLoading(false);
    }
  };

  const updateOccupancy = async () => {
    try {
      await axios.put(`${API_URL}/shelters/${shelter.id}/occupancy`, {
        occupancy: parseInt(occupancyInput),
        reason: 'Manual update'
      });
      Alert.alert('Success', 'Occupancy updated');
      fetchShelterData();
    } catch (e) {
      Alert.alert('Error', 'Failed to update occupancy');
    }
  };

  const confirmReceipt = async (allocationId, qty) => {
    try {
      await axios.post(`${API_URL}/resource-allocations/${allocationId}/confirm-receipt`, {
        receivedQuantity: qty
      });
      Alert.alert('Success', 'Resource receipt confirmed');
      fetchShelterData();
    } catch (e) {
      Alert.alert('Error', 'Failed to confirm receipt');
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Shelter Operations</Text>
        <Text style={styles.headerSubtitle}>{shelter?.name || 'Loading...'}</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#00796b" style={{ marginTop: 50 }} />
      ) : (
        <FlatList
          contentContainerStyle={styles.list}
          ListHeaderComponent={
            <View style={styles.card}>
              <Text style={styles.cardTitle}>Occupancy Status</Text>
              <Text style={styles.statusText}>Capacity: {shelter.maximumCapacity}</Text>
              <Text style={styles.statusText}>Current Status: {shelter.status.replace('_', ' ')}</Text>
              
              <View style={styles.inputRow}>
                <TextInput 
                  style={styles.input} 
                  value={occupancyInput}
                  onChangeText={setOccupancyInput}
                  keyboardType="numeric"
                />
                <TouchableOpacity style={styles.btnUpdate} onPress={updateOccupancy}>
                  <Text style={styles.buttonText}>Update Occupancy</Text>
                </TouchableOpacity>
              </View>
            </View>
          }
          data={allocations}
          keyExtractor={(item) => item.id.toString()}
          ListEmptyComponent={<Text style={styles.emptyText}>No incoming resources.</Text>}
          renderItem={({ item }) => (
            <View style={styles.card}>
              <View style={styles.cardHeader}>
                <Text style={styles.cardTitle}>{item.resource?.resourceType}</Text>
                <Text style={styles.badge}>{item.status}</Text>
              </View>
              <Text>Provider: {item.provider?.organizationName}</Text>
              <Text>Allocated Qty: {item.allocatedQuantity || item.requestedQuantity}</Text>
              
              {item.status === 'DISPATCHED' && (
                <TouchableOpacity style={styles.btnConfirm} onPress={() => confirmReceipt(item.id, item.allocatedQuantity || item.requestedQuantity)}>
                  <Text style={styles.buttonText}>Confirm Delivery Received</Text>
                </TouchableOpacity>
              )}
            </View>
          )}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#00796b', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#b2dfdb', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  list: { padding: 15 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 15, marginBottom: 15, elevation: 2 },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#00796b' },
  statusText: { fontSize: 14, color: '#444', marginBottom: 5 },
  inputRow: { flexDirection: 'row', alignItems: 'center', marginTop: 10 },
  input: { borderWidth: 1, borderColor: '#ccc', borderRadius: 5, padding: 8, width: 80, marginRight: 10, textAlign: 'center' },
  btnUpdate: { backgroundColor: '#00796b', padding: 10, borderRadius: 5 },
  badge: { backgroundColor: '#f57c00', paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12, fontSize: 10, fontWeight: 'bold', color: '#fff', overflow: 'hidden' },
  btnConfirm: { backgroundColor: '#388e3c', padding: 10, borderRadius: 5, marginTop: 15, alignItems: 'center' },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 13 },
  emptyText: { textAlign: 'center', marginTop: 20, color: '#666', fontStyle: 'italic' }
});

export default ShelterDashboard;
