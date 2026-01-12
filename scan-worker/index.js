const express = require('express');
const { exec } = require('child_process');
const fs = require('fs');

const app = express();
app.use(express.json());

app.post('/scan', (req, res) => {
  const { scanId, projectKey, projectType, sourceDir } = req.body;

  console.log('=== Receive scan job ===');
  console.log(req.body);

  // ตอบกลับทันที
  res.json({ status: 'QUEUED', scanId });

  let cmd = '';

  if (projectType === 'SPRING_BOOT') {
    cmd = `
      cd ${sourceDir} && \
      chmod +x mvnw && \
      ./mvnw -B -DskipTests compile && \
      sonar-scanner \
        -Dsonar.projectKey=${projectKey} \
        -Dsonar.sources=src/main/java \
        -Dsonar.java.binaries=target/classes \
        -Dsonar.host.url=${process.env.SONAR_HOST_URL} \
        -Dsonar.login=${process.env.SONAR_TOKEN}
    `;
  }

  else if (projectType === 'ANGULAR') {
    let tsconfig = 'tsconfig.json';
        if (fs.existsSync(`${safeSourceDir}/tsconfig.app.json`)) {
          tsconfig = 'tsconfig.app.json';
        }
    cmd = `
      cd ${sourceDir} && \
      sonar-scanner \
        -Dsonar.projectKey=${projectKey} \
        -Dsonar.sources=src \
        -Dsonar.exclusions=**/node_modules/**,**/*.spec.ts \
        -Dsonar.typescript.tsconfigPath=tsconfig.json \
        -Dsonar.host.url=${process.env.SONAR_HOST_URL} \
        -Dsonar.login=${process.env.SONAR_TOKEN}
    `;
  }

  else {
    // fallback สำหรับ project อื่น
    cmd = `
      cd ${sourceDir} && \
      sonar-scanner \
        -Dsonar.projectKey=${projectKey} \
        -Dsonar.sources=. \
        -Dsonar.host.url=${process.env.SONAR_HOST_URL} \
        -Dsonar.login=${process.env.SONAR_TOKEN}
    `;
  }

  exec(cmd, (error, stdout, stderr) => {
    if (error) {
      console.error('Scan failed');
      console.error('STDOUT:', stdout);
      console.error('STDERR:', stderr);
      return;
    }

    console.log('Scan success');
    console.log(stdout);
  });
});

app.listen(8090, () => {
  console.log('Scan-worker listening on port 8090');
});
