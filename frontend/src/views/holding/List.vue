<template>
  <div class="container-fluid" v-html="template"></div>
</template>

<script>
import api from '../../api';

export default {
  name: 'HoldingList',
  data() {
    return {
      holdings: [],
      template: ''
    };
  },
  async created() {
    const res = await fetch("/src/views/holding/templates/List.html");
    this.template = await res.text();
    this.fetchHoldings();
  },
  methods: {
    async fetchHoldings() {
      try {
        const response = await api.get('/api/holdings');
        this.holdings = response.data;
      } catch (error) {
        console.error('Error fetching holdings:', error);
      }
    }
  }
};
</script>
