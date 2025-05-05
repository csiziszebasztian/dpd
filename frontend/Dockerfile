# Stage 1: Install dependencies
FROM oven/bun:1 as deps
WORKDIR /app

# Copy package.json and bun.lockb
COPY package.json bun.lockb* ./

# Install dependencies
RUN bun install --frozen-lockfile

# Stage 2: Build the application
FROM deps as builder
WORKDIR /app

# Copy the rest of the application code
COPY . .

# Set environment variable for API URL during build time (if needed by build process)
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL}

# Build the Next.js application
RUN bun run build

# Stage 3: Production image
FROM oven/bun:1 as runner
WORKDIR /app

# Set environment variable for API URL at runtime
# The NEXT_PUBLIC_API_URL will be passed from docker-compose.yml
ENV NODE_ENV=production
# ENV NEXT_PUBLIC_API_URL=${NEXT_PUBLIC_API_URL} # Already set by docker-compose

# Copy necessary files from the builder stage
COPY --from=builder /app/public ./public
COPY --from=builder --chown=bun:bun /app/.next/standalone ./
COPY --from=builder --chown=bun:bun /app/.next/static ./.next/static

# Expose the port the app runs on
EXPOSE 3000

# Set the user to run the application
USER bun

# Command to run the application
# CMD ["bun", "run", "start"]
# Note: Next.js standalone output uses server.js, bun might handle this implicitly
# If issues arise, try: CMD ["node", "server.js"]
CMD ["node", "server.js"]
