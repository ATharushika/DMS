import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, FlatList, ActivityIndicator } from 'react-native';
import { AuthContext } from '../../context/AuthContext';
import SubmitReportModal from '../../components/SubmitReportModal';
import axios from 'axios';

const CitizenDashboard = () => {
  const { user, logout, API_URL } = useContext(AuthContext);
  const [warnings, setWarnings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalVisible, setModalVisible] = useState(false);

  useEffect(() => {
    fetchActiveWarnings();
  }, []);

  const fetchActiveWarnings = async () => {
    try {
      const response = await axios.get(`${API_URL}/warnings/active`);
      setWarnings(response.data);
    } catch (e) {
      console.log('Error fetching warnings', e);
    } finally {
      setLoading(false);
    }
  };

  const handleAction = async (id, action) => {
    try {
      if (action === 'delete') await axios.delete(`${API_URL}/hazards/${id}`);
      fetchActiveWarnings();
      Alert.alert('Success', `Report ${action} successful`);
    } catch (e) {
      Alert.alert('Error', `Failed to ${action} report`);
    }
  };

  const confirmDelete = (id) => {
    Alert.alert('Confirm Delete', 'Are you sure you want to delete this report?', [
      { text: 'Cancel', style: 'cancel' },
      { text: 'Delete', style: 'destructive', onPress: () => handleAction(id, 'delete') }
    ]);
  };

  const renderWarning = ({ item }) => (
    <View style={[styles.card, item.warningLevel === 'CRITICAL' ? styles.cardCritical : styles.cardWarning]}>
      <Text style={styles.cardTitle}>{item.hazardType} - {item.warningLevel}</Text>
      <Text style={styles.cardArea}>Affected Area: {item.affectedArea}</Text>
      <Text style={styles.cardMessage}>{item.message}</Text>
      {item.emergencyInstructions ? (
        <View style={styles.instructionsBox}>
          <Text style={styles.instructionsTitle}>Emergency Instructions:</Text>
          <Text style={styles.instructionsText}>{item.emergencyInstructions}</Text>
        </View>
      ) : null}
      <Text style={styles.cardTime}>Issued: {new Date(item.issuedAt || item.createdAt).toLocaleString()}</Text>
    </View>
  );

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Citizen Dashboard</Text>
        <Text style={styles.headerSubtitle}>Welcome, {user?.name}</Text>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Active Warnings</Text>
        {loading ? (
          <ActivityIndicator size="large" color="#1a73e8" />
        ) : warnings.length === 0 ? (
          <Text style={styles.emptyText}>No active warnings in your area. Stay safe!</Text>
        ) : (
          <FlatList
            data={warnings}
            renderItem={renderWarning}
            keyExtractor={(item) => item.id.toString()}
            scrollEnabled={false}
          />
        )}
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Quick Actions</Text>
        <View style={styles.actionsGrid}>
          <TouchableOpacity style={styles.actionButton} onPress={() => setModalVisible(true)}>
            <Text style={styles.actionButtonText}>Report Hazard</Text>
          </TouchableOpacity>
        </View>
      </View>

      <TouchableOpacity style={styles.logoutButton} onPress={logout}>
        <Text style={styles.logoutButtonText}>Log Out</Text>
      </TouchableOpacity>
      
      <SubmitReportModal 
        visible={modalVisible} 
        onClose={() => setModalVisible(false)} 
        onSuccess={() => console.log('Report submitted')} 
      />
      <View style={{ height: 40 }} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { backgroundColor: '#1a73e8', padding: 20, paddingTop: 50, borderBottomLeftRadius: 15, borderBottomRightRadius: 15 },
  headerTitle: { color: '#fff', fontSize: 24, fontWeight: 'bold' },
  headerSubtitle: { color: '#e0e0e0', fontSize: 16, marginTop: 5 },
  section: { padding: 20 },
  sectionTitle: { fontSize: 18, fontWeight: 'bold', color: '#333', marginBottom: 15 },
  card: { backgroundColor: '#fff', borderRadius: 10, padding: 15, marginBottom: 15, elevation: 3, borderLeftWidth: 5 },
  cardCritical: { borderLeftColor: '#d32f2f' },
  cardWarning: { borderLeftColor: '#f57c00' },
  cardTitle: { fontSize: 16, fontWeight: 'bold', marginBottom: 5, color: '#333' },
  cardArea: { fontSize: 14, color: '#666', marginBottom: 10 },
  cardMessage: { fontSize: 14, color: '#444', marginBottom: 10, lineHeight: 20 },
  instructionsBox: { backgroundColor: '#fff3e0', padding: 10, borderRadius: 5, marginBottom: 10 },
  instructionsTitle: { fontWeight: 'bold', color: '#e65100', marginBottom: 5 },
  instructionsText: { color: '#e65100', fontSize: 13 },
  cardTime: { fontSize: 12, color: '#999', textAlign: 'right' },
  emptyText: { color: '#666', fontStyle: 'italic', textAlign: 'center', padding: 20 },
  actionsGrid: { flexDirection: 'row', justifyContent: 'flex-start', flexWrap: 'wrap' },
  actionButton: { backgroundColor: '#fff', width: '31%', aspectRatio: 1, justifyContent: 'center', alignItems: 'center', borderRadius: 10, elevation: 2, marginBottom: 10 },
  actionButtonText: { color: '#1a73e8', fontWeight: 'bold', textAlign: 'center' },
  logoutButton: { backgroundColor: '#d32f2f', margin: 20, padding: 15, borderRadius: 8, alignItems: 'center' },
  logoutButtonText: { color: '#fff', fontWeight: 'bold', fontSize: 16 }
});

export default CitizenDashboard;
