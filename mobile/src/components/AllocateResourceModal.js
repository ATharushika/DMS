import React, { useContext, useEffect, useState } from 'react';
import { View, Text, StyleSheet, Modal, TextInput, TouchableOpacity, ActivityIndicator, Alert, ScrollView } from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';

const AllocateResourceModal = ({ visible, onClose, onSuccess }) => {
  const { API_URL } = useContext(AuthContext);
  const [shelters, setShelters] = useState([]);
  const [providers, setProviders] = useState([]);
  const [resources, setResources] = useState([]);
  
  const [shelterId, setShelterId] = useState('');
  const [providerId, setProviderId] = useState('');
  const [resourceId, setResourceId] = useState('');
  const [quantity, setQuantity] = useState('100');
  
  const [loadingData, setLoadingData] = useState(true);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (visible) fetchData();
  }, [visible]);

  const fetchData = async () => {
    setLoadingData(true);
    try {
      const [shRes, prRes, reRes] = await Promise.all([
        axios.get(`${API_URL}/shelters`),
        axios.get(`${API_URL}/providers`),
        axios.get(`${API_URL}/resources`)
      ]);
      setShelters(shRes.data);
      setProviders(prRes.data);
      setResources(reRes.data);
      
      if (shRes.data.length > 0) setShelterId(shRes.data[0].id.toString());
      if (prRes.data.length > 0) setProviderId(prRes.data[0].id.toString());
      if (reRes.data.length > 0) setResourceId(reRes.data[0].id.toString());
    } catch (e) {
      Alert.alert('Error', 'Failed to fetch allocation data');
    } finally {
      setLoadingData(false);
    }
  };

  const handleSubmit = async () => {
    if (!shelterId || !providerId || !resourceId || !quantity) {
      Alert.alert('Validation', 'Please fill all fields');
      return;
    }

    setLoading(true);
    try {
      await axios.post(`${API_URL}/resource-allocations`, {
        shelterId: parseInt(shelterId),
        providerId: parseInt(providerId),
        resourceId: parseInt(resourceId),
        quantity: parseInt(quantity)
      });

      Alert.alert('Success', 'Resource requested successfully.');
      onSuccess();
      onClose();
    } catch (e) {
      Alert.alert('Error', 'Failed to request resource.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal visible={visible} animationType="slide" transparent={true}>
      <View style={styles.overlay}>
        <View style={styles.modalContent}>
          <Text style={styles.title}>Request Relief Resources</Text>

          {loadingData ? <ActivityIndicator size="large" /> : (
            <ScrollView>
              <Text style={styles.label}>Target Shelter</Text>
              <View style={styles.pickerContainer}>
                <Picker selectedValue={shelterId} onValueChange={setShelterId}>
                  {shelters.map(s => <Picker.Item key={s.id} label={s.name} value={s.id.toString()} />)}
                </Picker>
              </View>

              <Text style={styles.label}>Resource Needed</Text>
              <View style={styles.pickerContainer}>
                <Picker selectedValue={resourceId} onValueChange={setResourceId}>
                  {resources.map(r => <Picker.Item key={r.id} label={r.resourceType} value={r.id.toString()} />)}
                </Picker>
              </View>
              
              <Text style={styles.label}>Resource Provider</Text>
              <View style={styles.pickerContainer}>
                <Picker selectedValue={providerId} onValueChange={setProviderId}>
                  {providers.map(p => <Picker.Item key={p.id} label={p.organizationName} value={p.id.toString()} />)}
                </Picker>
              </View>

              <Text style={styles.label}>Quantity</Text>
              <TextInput
                style={styles.input}
                keyboardType="numeric"
                value={quantity}
                onChangeText={setQuantity}
              />

              <View style={styles.actionRow}>
                <TouchableOpacity style={styles.cancelBtn} onPress={onClose} disabled={loading}>
                  <Text style={styles.cancelBtnText}>Cancel</Text>
                </TouchableOpacity>
                <TouchableOpacity style={styles.submitBtn} onPress={handleSubmit} disabled={loading}>
                  {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.submitBtnText}>Request Resource</Text>}
                </TouchableOpacity>
              </View>
            </ScrollView>
          )}
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center', padding: 20 },
  modalContent: { backgroundColor: '#fff', borderRadius: 10, padding: 20, elevation: 5, maxHeight: '80%' },
  title: { fontSize: 20, fontWeight: 'bold', color: '#00695c', marginBottom: 20, textAlign: 'center' },
  label: { fontSize: 14, color: '#555', marginBottom: 5, fontWeight: 'bold' },
  input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, padding: 10, marginBottom: 15, backgroundColor: '#f9f9f9' },
  pickerContainer: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, marginBottom: 15, backgroundColor: '#f9f9f9' },
  actionRow: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 10 },
  cancelBtn: { flex: 1, padding: 15, backgroundColor: '#e0e0e0', borderRadius: 5, marginRight: 10, alignItems: 'center' },
  cancelBtnText: { color: '#333', fontWeight: 'bold' },
  submitBtn: { flex: 1, padding: 15, backgroundColor: '#00695c', borderRadius: 5, marginLeft: 10, alignItems: 'center' },
  submitBtnText: { color: '#fff', fontWeight: 'bold' }
});

export default AllocateResourceModal;
