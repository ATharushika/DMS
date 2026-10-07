import React, { useContext, useState } from 'react';
import { View, Text, StyleSheet, Modal, TextInput, TouchableOpacity, ActivityIndicator, Alert, ScrollView } from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';

const CreateIncidentModal = ({ visible, onClose, onSuccess }) => {
  const { API_URL } = useContext(AuthContext);
  const [description, setDescription] = useState('');
  const [locationName, setLocationName] = useState('');
  const [priority, setPriority] = useState('HIGH');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (!description || !locationName) {
      Alert.alert('Validation', 'Please provide description and location.');
      return;
    }

    setLoading(true);
    try {
      await axios.post(`${API_URL}/incidents`, {
        description,
        locationName,
        priority,
        latitude: 6.9271,
        longitude: 79.8612
      });

      Alert.alert('Success', 'Incident opened successfully.');
      
      setDescription('');
      setLocationName('');
      onSuccess();
      onClose();
    } catch (e) {
      Alert.alert('Error', 'Failed to create incident.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal visible={visible} animationType="slide" transparent={true}>
      <View style={styles.overlay}>
        <View style={styles.modalContent}>
          <Text style={styles.title}>Open New Incident</Text>

          <Text style={styles.label}>Description</Text>
          <TextInput
            style={[styles.input, { height: 80, textAlignVertical: 'top' }]}
            multiline
            placeholder="Describe the emergency..."
            value={description}
            onChangeText={setDescription}
          />

          <Text style={styles.label}>Location Name</Text>
          <TextInput
            style={styles.input}
            placeholder="e.g. Kandy Town"
            value={locationName}
            onChangeText={setLocationName}
          />

          <Text style={styles.label}>Priority Level</Text>
          <View style={styles.pickerContainer}>
            <Picker selectedValue={priority} onValueChange={setPriority}>
              <Picker.Item label="Critical" value="CRITICAL" />
              <Picker.Item label="High" value="HIGH" />
              <Picker.Item label="Medium" value="MEDIUM" />
              <Picker.Item label="Low" value="LOW" />
            </Picker>
          </View>

          <View style={styles.actionRow}>
            <TouchableOpacity style={styles.cancelBtn} onPress={onClose} disabled={loading}>
              <Text style={styles.cancelBtnText}>Cancel</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.submitBtn} onPress={handleSubmit} disabled={loading}>
              {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.submitBtnText}>Create Incident</Text>}
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center', padding: 20 },
  modalContent: { backgroundColor: '#fff', borderRadius: 10, padding: 20, elevation: 5 },
  title: { fontSize: 20, fontWeight: 'bold', color: '#00695c', marginBottom: 20, textAlign: 'center' },
  label: { fontSize: 14, color: '#555', marginBottom: 5, fontWeight: 'bold' },
  input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, padding: 10, marginBottom: 15, backgroundColor: '#f9f9f9' },
  pickerContainer: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, marginBottom: 15, backgroundColor: '#f9f9f9' },
  actionRow: { flexDirection: 'row', justifyContent: 'space-between' },
  cancelBtn: { flex: 1, padding: 15, backgroundColor: '#e0e0e0', borderRadius: 5, marginRight: 10, alignItems: 'center' },
  cancelBtnText: { color: '#333', fontWeight: 'bold' },
  submitBtn: { flex: 1, padding: 15, backgroundColor: '#00695c', borderRadius: 5, marginLeft: 10, alignItems: 'center' },
  submitBtnText: { color: '#fff', fontWeight: 'bold' }
});

export default CreateIncidentModal;
