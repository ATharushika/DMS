import React, { useContext, useState, useEffect } from 'react';
import { View, Text, StyleSheet, Modal, TextInput, TouchableOpacity, ActivityIndicator, Alert, ScrollView } from 'react-native';
import { Picker } from '@react-native-picker/picker';
import { AuthContext } from '../context/AuthContext';
import axios from 'axios';

const CreateWarningModal = ({ visible, onClose, onSuccess, sourceHazard }) => {
  const { API_URL } = useContext(AuthContext);
  const [hazardType, setHazardType] = useState('OTHER');
  const [warningLevel, setWarningLevel] = useState('INFORMATION');
  const [affectedArea, setAffectedArea] = useState('');
  const [message, setMessage] = useState('');
  const [instructions, setInstructions] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (sourceHazard) {
      setHazardType(sourceHazard.hazardType);
      setAffectedArea(sourceHazard.locationName || '');
      setMessage(`Alert: ${sourceHazard.hazardType} reported in ${sourceHazard.locationName}. Please stay vigilant.`);
    }
  }, [sourceHazard]);

  const handleSubmit = async () => {
    if (!affectedArea || !message) {
      Alert.alert('Validation', 'Please provide affected area and message.');
      return;
    }

    setLoading(true);
    try {
      await axios.post(`${API_URL}/warnings`, {
        hazardType,
        warningLevel,
        affectedArea,
        message,
        emergencyInstructions: instructions,
        notificationChannels: ['PUSH_NOTIFICATION', 'SMS'],
        sourceReportId: sourceHazard ? sourceHazard.id : null
      });

      Alert.alert('Success', 'Warning created as DRAFT.');
      onSuccess();
      onClose();
    } catch (e) {
      console.log(e);
      Alert.alert('Error', 'Failed to create warning.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal visible={visible} animationType="slide" transparent={true}>
      <View style={styles.overlay}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <View style={styles.modalContent}>
            <Text style={styles.title}>Create New Warning</Text>

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

            <Text style={styles.label}>Warning Level</Text>
            <View style={styles.pickerContainer}>
              <Picker selectedValue={warningLevel} onValueChange={setWarningLevel}>
                <Picker.Item label="Information" value="INFORMATION" />
                <Picker.Item label="Advisory" value="ADVISORY" />
                <Picker.Item label="Watch" value="WATCH" />
                <Picker.Item label="Warning" value="WARNING" />
                <Picker.Item label="Critical" value="CRITICAL" />
              </Picker>
            </View>

            <Text style={styles.label}>Affected Area</Text>
            <TextInput
              style={styles.input}
              placeholder="e.g. Colombo District"
              value={affectedArea}
              onChangeText={setAffectedArea}
            />

            <Text style={styles.label}>Warning Message</Text>
            <TextInput
              style={[styles.input, { height: 60, textAlignVertical: 'top' }]}
              multiline
              value={message}
              onChangeText={setMessage}
            />

            <Text style={styles.label}>Emergency Instructions</Text>
            <TextInput
              style={[styles.input, { height: 60, textAlignVertical: 'top' }]}
              multiline
              placeholder="e.g. Evacuate to higher ground immediately."
              value={instructions}
              onChangeText={setInstructions}
            />

            <View style={styles.actionRow}>
              <TouchableOpacity style={styles.cancelBtn} onPress={onClose} disabled={loading}>
                <Text style={styles.cancelBtnText}>Cancel</Text>
              </TouchableOpacity>
              
              <TouchableOpacity style={styles.submitBtn} onPress={handleSubmit} disabled={loading}>
                {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.submitBtnText}>Create Draft</Text>}
              </TouchableOpacity>
            </View>
          </View>
        </ScrollView>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  overlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center' },
  scrollContent: { flexGrow: 1, justifyContent: 'center', padding: 20 },
  modalContent: { backgroundColor: '#fff', borderRadius: 10, padding: 20, elevation: 5 },
  title: { fontSize: 20, fontWeight: 'bold', color: '#d84315', marginBottom: 20, textAlign: 'center' },
  label: { fontSize: 14, color: '#555', marginBottom: 5, fontWeight: 'bold' },
  pickerContainer: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, marginBottom: 15, backgroundColor: '#f9f9f9' },
  input: { borderWidth: 1, borderColor: '#ddd', borderRadius: 5, padding: 10, marginBottom: 15, backgroundColor: '#f9f9f9' },
  actionRow: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 10 },
  cancelBtn: { flex: 1, padding: 15, backgroundColor: '#e0e0e0', borderRadius: 5, marginRight: 10, alignItems: 'center' },
  cancelBtnText: { color: '#333', fontWeight: 'bold' },
  submitBtn: { flex: 1, padding: 15, backgroundColor: '#d84315', borderRadius: 5, marginLeft: 10, alignItems: 'center' },
  submitBtnText: { color: '#fff', fontWeight: 'bold' }
});

export default CreateWarningModal;
