import React, { useContext } from 'react';
import { View, Text, StyleSheet, Button } from 'react-native';
import { AuthContext } from '../../context/AuthContext';

const DashboardPlaceholder = ({ route, name }) => {
  const { user, logout } = useContext(AuthContext);
  return (
    <View style={styles.container}>
      <Text style={styles.title}>{name}</Text>
      <Text style={styles.subtitle}>Welcome, {user?.name} ({user?.role})</Text>
      <View style={styles.content}>
        <Text>Full features coming soon.</Text>
      </View>
      <Button title="Logout" onPress={logout} color="#d32f2f" />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, marginTop: 40 },
  title: { fontSize: 24, fontWeight: 'bold' },
  subtitle: { fontSize: 16, color: '#666', marginBottom: 20 },
  content: { flex: 1, justifyContent: 'center', alignItems: 'center' }
});

export default DashboardPlaceholder;
