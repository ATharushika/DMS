import React, { useContext, useState } from 'react';
import { View, Text, StyleSheet, Modal, TextInput, TouchableOpacity, ActivityIndicator, Alert } from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';

const SubmitReportModal = ({ visible, onClose, onSuccess }) => {
  const { API_URL } = useContext(AuthContext);
  const [hazardType, setHazardType] = useState('FLOOD');
  const [description, setDescription] = useState('');
  const [locationName, setLocationName] = useState('');
  const [loading, setLoading] = useState(false);
  const [gpsMocked, setGpsMocked] = useState(false);
  
  // Default coordinates (e.g., Colombo, Sri Lanka)
  const [latitude, setLatitude] = useState(null);
  const [longitude, setLongitude] = useState(null);

  const handleMockGps = () => {
    // Simulating GPS capture (University project requirement)
    setLatitude(6.927079);
    setLongitude(79.861244);
    setGpsMocked(true);
    Alert.alert('GPS Captured', 'Mock coordinates loaded successfully.');
  };

  const handleSubmit = async () => {
    if (!description || !locationName) {
      Alert.alert('Validation', 'Please provide a description and location name.');
      return;
    }

    setLoading(true);
    try {
      // 1. Optional duplicate check (as requested in Assignment 2)
      const dupCheck = await axios.post(`${API_URL}/hazards/check-duplicate`, {
        hazardType, latitude, longitude
      });

      if (dupCheck.data && dupCheck.data.length > 0) {
        // We found duplicates, prompt user but continue for this demo if they want
        // For simplicity, we just log it and proceed submitting.
        console.log('Similar hazard reports found nearby.');
      }

      // 2. Submit actual report
      await axios.post(`${API_URL}/hazards`, {
        hazardType,
        description,
        locationName,
        latitude,
        longitude
      });

      Alert.alert('Success', 'Hazard report submitted successfully!');
      
      // Reset form
      setDescription('');
      setLocationName('');
      setGpsMocked(false);
      setLatitude(null);
      setLongitude(null);
      
      onSuccess(); // Refresh parent lists
      onClose();   // Close modal
    } catch (e) {
      console.log(e);
      Alert.alert('Error', 'Failed to submit report. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal visible={visible} animationType="slide" transparent={true}>
      <View style={styles.overlay}>
        <View style={styles.modalContent}>
          <Text style={styles.title}>Submit Hazard Report</Text>

          <Text style={styles.label}>Hazard Type</Text>
          <View style={styles.pickerContainer}>
            <Picker selectedValue={hazardType} onValueChange={setHazardType}>
              <Picker.Item label="Flood" value="FLOOD" />
              <Picker.Item label="Landslide" value="LANDSLIDE" />
              <Picker.Item label="Cyclone" value="CYCLONE" />
              <Picker.Item label="Fire" value="FIRE" />
              <Picker.Item label="Other" value="OTHER" />
            </Picker>
          </View>

          <Text style={styles.label}>Description</Text>
          <TextInput
            style={[styles.input, { height: 80, textAlignVertical: 'top' }]}
            multiline
            placeholder="Describe the situation..."
            value={description}
            onChangeText={setDescription}
          />

          <Text style={styles.label}>Location Name</Text>
          <TextInput
            style={styles.input}
            placeholder="e.g. Main Street, Colombo 01"
            value={locationName}
            onChangeText={setLocationName}
          />

          <TouchableOpacity style={[styles.gpsBtn, gpsMocked ? styles.gpsBtnSuccess : null]} onPress={handleMockGps}>
            <Text style={styles.gpsBtnText}>{gpsMocked ? '✓ GPS Captured' : 'Capture GPS (Mock)'}</Text>
          </TouchableOpacity>

          <View style={styles.actionRow}>
            <TouchableOpacity style={styles.cancelBtn} onPress={onClose} disabled={loading}>
              <Text style={styles.cancelBtnText}>Cancel</Text>
            </TouchableOpacity>
            
            <TouchableOpacity style={styles.submitBtn} onPress={handleSubmit} disabled={loading}>
              {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.submitBtnText}>Submit Report</Text>}
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
  title: { fontSize: 20, fontWeight: 'bold', color: '#1a73e8', marginBottom: 20, textAlign: 'center' },
  label: { fontSize: 14, color: '#555', marginBottom: 5, fontWeight: 'bold' },
  pickerContainer: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, marginBottom: 15, backgroundColor: '#f9f9f9' },
  input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, padding: 10, marginBottom: 15, backgroundColor: '#f9f9f9' },
  gpsBtn: { backgroundColor: '#ff9800', padding: 12, borderRadius: 5, alignItems: 'center', marginBottom: 20 },
  gpsBtnSuccess: { backgroundColor: '#4caf50' },
  gpsBtnText: { color: '#fff', fontWeight: 'bold' },
  actionRow: { flexDirection: 'row', justifyContent: 'space-between' },
  cancelBtn: { flex: 1, padding: 15, backgroundColor: '#e0e0e0', borderRadius: 5, marginRight: 10, alignItems: 'center' },
  cancelBtnText: { color: '#333', fontWeight: 'bold' },
  submitBtn: { flex: 1, padding: 15, backgroundColor: '#1a73e8', borderRadius: 5, marginLeft: 10, alignItems: 'center' },
  submitBtnText: { color: '#fff', fontWeight: 'bold' }
});

export default SubmitReportModal;
