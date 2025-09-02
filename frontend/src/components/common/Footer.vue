<template src="./templates/Footer.html"></template>

<script>
export default {
  name: 'Footer',
  data() {
    return {
      currentYear: new Date().getFullYear(),
      systemInfo: {
        name: '管理システム',
        version: '1.0.0'
      }
    };
  }
};
</script>

<style scoped>
.footer {
  background-color: #f8f9fa;
  border-top: 1px solid #dee2e6;
  padding: 2rem 0 1rem;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.footer-info {
  color: #6c757d;
  font-size: 0.9rem;
}

.footer-links {
  display: flex;
  gap: 20px;
  list-style: none;
  margin: 0;
  padding: 0;
}

.footer-links a {
  color: #6c757d;
  text-decoration: none;
  font-size: 0.9rem;
}

.footer-links a:hover {
  color: #007bff;
}

.copyright {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
  color: #6c757d;
  font-size: 0.8rem;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
  }
}
</style>
