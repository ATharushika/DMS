import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList, ActivityIndicator, Alert } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import axios from 'axios';

const ResourceProviderDashboard = () => {
  const { user, logout, API_URL } = useContext(AuthContext);
  const [allocations, setAllocations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [providerId, setProviderId] = useState(null);

  useEffect(() => {
    initProviderData();
  }, []);

  const initProviderData = async () => {
    try {
      const provRes = await axios.get(`${API_URL}/providers`);
      const myProv = provRes.data.find(p => p.user?.id === user.userId);
      if (myProv) {
        setProviderId(myProv.id);
        fetchAllocations(myProv.id);
      } else {
        setLoading(false);
      }
    } catch (e) {
      setLoading(false);
    }
  };

  const fetchAllocations = async (id) => {
    try {
      const response = await axios.get(`${API_URL}/resource-allocations?providerId=${id}`);
      setAllocations(response.data.filter(a => a.status === 'REQUESTED' || a.status === 'DISPATCHED'));
    } catch (e) {
      console.log('Error fetching allocations', e);
    } finally {
      setLoading(false);
    }
  };

  const dispatchResource = async (allocationId) => {
    try {
      await axios.put(`${API_URL}/resource-allocations/${allocationId}/status`, {
        status: 'DISPATCHED'
      });
      Alert.alert('Success', 'Resource marked as dispatched');
      fetchAllocations(providerId);
    } catch (e) {
      Alert.alert('Error', 'Failed to dispatch resource');
    }
  };

  const renderAllocation = ({ item }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.cardTitle}>{item.resource?.resourceType}</Text>
        <Text style={[styles.badge, styles[`badge_${item.status}`]]}>{item.status}</Text>
      </View>
      <Text style={styles.cardText}>Destination: {item.shelter?.name}</Text>
      <Text style={styles.cardText}>Quantity Requested: {item.requestedQuantity}</Text>
      
      {item.status === 'REQUESTED' && (
        <TouchableOpacity style={styles.btnDispatch} onPress={() => dispatchResource(item.id)}>
          <Text style={styles.buttonText}>Dispatch Delivery</Text>
        </TouchableOpacity>
      )}
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Resource Provider</Text>
        <Text style={styles.headerSubtitle}>Manage Relief Dispatches</Text>
        <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
          <Text style={styles.logoutBtnText}>Logout</Text>
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator size="large" color="#f57c00" style={{ marginTop: 50 }} />
      ) : (
        <FlatList
          contentContainerStyle={styles.list}
          data={allocations}
          renderItem={renderAllocation}
          keyExtractor={(item) => item.id.toString()}
          ListEmptyComponent={<Text style={styles.emptyText}>No pending requests.</Text>}
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#e65100', padding: 20, paddingTop: 50, paddingBottom: 20, position: 'relative' },
  headerTitle: { color: '#fff', fontSize: 22, fontWeight: 'bold' },
  headerSubtitle: { color: '#ffe0b2', fontSize: 14, marginTop: 5 },
  logoutBtn: { position: 'absolute', right: 20, top: 50, backgroundColor: 'rgba(255,255,255,0.2)', padding: 8, borderRadius: 5 },
  logoutBtnText: { color: '#fff', fontWeight: 'bold' },
  list: { padding: 15 },
  card: { backgroundColor: '#fff', borderRadius: 8, padding: 15, marginBottom: 15, elevation: 2, borderLeftWidth: 5, borderLeftColor: '#e65100' },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  cardTitle: { fontSize: 16, fontWeight: 'bold', color: '#333' },
  badge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 12, fontSize: 10, fontWeight: 'bold', color: '#fff', overflow: 'hidden' },
  badge_REQUESTED: { backgroundColor: '#d32f2f' },
  badge_DISPATCHED: { backgroundColor: '#f57c00' },
  cardText: { fontSize: 14, color: '#444', marginBottom: 5 },
  btnDispatch: { backgroundColor: '#1976d2', padding: 12, borderRadius: 5, marginTop: 15, alignItems: 'center' },
  buttonText: { color: '#fff', fontWeight: 'bold', fontSize: 13 },
  emptyText: { textAlign: 'center', marginTop: 50, color: '#666', fontStyle: 'italic' }
});

export default ResourceProviderDashboard;
