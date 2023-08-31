import pkg from './package.json' assert { type: "json" };;

export default [
	{
		input: 'src/main.mjs',
		output: {
			name: 'izanami-script-core',
			file: pkg.module,
			format: 'esm'
		}
	}
];
